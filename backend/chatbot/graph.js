const { StateGraph, START, END } = require('@langchain/langgraph');
const { Annotation } = require('@langchain/langgraph');
const { GoogleGenerativeAI } = require('@google/generative-ai');
const { buildSynthesisPrompt } = require('./prompts');
const { searchEvents } = require('./vectorStore');

const ChatbotState = Annotation.Root({
    userMessage: Annotation({ reducer: (_, v) => v, default: () => '' }),
    userId: Annotation({ reducer: (_, v) => v, default: () => null }),
    conversationHistory: Annotation({ reducer: (_, v) => v, default: () => [] }),
    retrievedEvents: Annotation({ reducer: (_, v) => v, default: () => [] }),
    synthesizedResponse: Annotation({ reducer: (_, v) => v, default: () => null }),
    finalResponse: Annotation({ reducer: (_, v) => v, default: () => null }),
});

function parseJsonResponse(text) {
    // Try direct parse
    try {
        return JSON.parse(text);
    } catch (_) {}

    // Try extracting from markdown fences
    const fenceMatch = text.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (fenceMatch) {
        try {
            return JSON.parse(fenceMatch[1].trim());
        } catch (_) {}
    }

    // Try finding first { to last }
    const firstBrace = text.indexOf('{');
    const lastBrace = text.lastIndexOf('}');
    if (firstBrace !== -1 && lastBrace > firstBrace) {
        try {
            return JSON.parse(text.substring(firstBrace, lastBrace + 1));
        } catch (_) {}
    }

    return null;
}

function createChatbotGraph(db) {
    const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
    const model = genAI.getGenerativeModel({ model: 'gemini-2.5-flash' });

    // Node 1: Retrieval - semantic search for relevant events
    async function retrievalNode(state) {
        try {
            const events = await searchEvents(state.userMessage, 5);
            return { retrievedEvents: events };
        } catch (e) {
            console.error('Retrieval error:', e);
            return { retrievedEvents: [] };
        }
    }

    // Node 2: Synthesis - call Gemini with retrieved context
    async function synthesisNode(state) {
        try {
            const prompt = buildSynthesisPrompt(
                state.userMessage,
                state.conversationHistory,
                state.retrievedEvents
            );

            const result = await model.generateContent(prompt);
            const responseText = result.response.text().trim();

            const parsed = parseJsonResponse(responseText);

            if (parsed && parsed.type) {
                // For events type, attach the retrieved events
                if (parsed.type === 'events' && state.retrievedEvents.length > 0) {
                    parsed.events = state.retrievedEvents;
                }
                return { synthesizedResponse: parsed };
            }

            // Fallback to text response
            return {
                synthesizedResponse: {
                    type: 'text',
                    message: responseText.replace(/```json\s*|\s*```/g, '').trim() || 'Sorry, I had trouble understanding that. Could you try again?'
                }
            };
        } catch (e) {
            console.error('Synthesis error:', e);
            return {
                synthesizedResponse: {
                    type: 'text',
                    message: 'Sorry, I encountered an error. Please try again.'
                }
            };
        }
    }

    // Node 3: Constraint checker - verify event data from DB
    async function constraintCheckerNode(state) {
        const response = state.synthesizedResponse;

        if (!response || response.type !== 'events' || !response.events || response.events.length === 0) {
            return { finalResponse: response };
        }

        // Verify events exist and have correct participant counts
        return new Promise((resolve) => {
            const eventIds = response.events.map(e => e.id).filter(Boolean);

            if (eventIds.length === 0) {
                return resolve({ finalResponse: response });
            }

            const placeholders = eventIds.map(() => '?').join(',');
            db.all(
                `SELECT id, currentParticipants, maxParticipants FROM events WHERE id IN (${placeholders})`,
                eventIds,
                (err, rows) => {
                    if (err) {
                        console.error('Constraint check error:', err);
                        return resolve({ finalResponse: response });
                    }

                    const dbMap = {};
                    for (const row of rows) {
                        dbMap[row.id] = row;
                    }

                    // Update with real-time data and filter out deleted events
                    const verifiedEvents = response.events.filter(event => {
                        if (!event.id || !dbMap[event.id]) return false;
                        event.currentParticipants = dbMap[event.id].currentParticipants;
                        event.maxParticipants = dbMap[event.id].maxParticipants;
                        return true;
                    });

                    if (verifiedEvents.length === 0) {
                        return resolve({
                            finalResponse: {
                                type: 'text',
                                message: 'Sorry, I couldn\'t find any matching events right now. Try searching for something else!'
                            }
                        });
                    }

                    response.events = verifiedEvents;
                    response.message = `I found ${verifiedEvents.length} event(s) for you! Tap any card to view details:`;
                    resolve({ finalResponse: response });
                }
            );
        });
    }

    // Build the graph
    const graph = new StateGraph(ChatbotState)
        .addNode('retrieval', retrievalNode)
        .addNode('synthesis', synthesisNode)
        .addNode('constraintChecker', constraintCheckerNode)
        .addEdge(START, 'retrieval')
        .addEdge('retrieval', 'synthesis')
        .addEdge('synthesis', 'constraintChecker')
        .addEdge('constraintChecker', END);

    return graph.compile();
}

module.exports = { createChatbotGraph };
