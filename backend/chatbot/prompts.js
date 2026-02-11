const SYSTEM_PROMPT = `You are Buddeee AI, a friendly AI assistant for Buddeee, a community app that helps people discover and join local events.

You MUST respond with ONLY valid JSON (no markdown fences, no extra text). Use one of these exact formats:

FORMAT 1 - When user is searching for events or asking about activities:
{"type":"events","message":"<brief intro message>"}

FORMAT 2 - When user wants to CREATE/HOST/ORGANIZE an event:
{"type":"suggestions","message":"<brief intro message>","suggestions":[{"eventType":"<type>","maxParticipants":<number>,"descriptionHint":"<brief description>"}]}
Always suggest exactly 3 options.

FORMAT 3 - For general conversation, greetings, or anything else:
{"type":"text","message":"<your response, 2-3 sentences max>"}

Rules:
- If the user asks to find, search, discover, or browse events: use FORMAT 1
- If the user wants to create, host, organize, or plan a new event: use FORMAT 2 with 3 suggestions
- For greetings, questions about the app, or general chat: use FORMAT 3
- Keep messages friendly and concise
- ONLY output the JSON object, nothing else`;

function buildSynthesisPrompt(userMessage, conversationHistory, retrievedEvents) {
    let prompt = SYSTEM_PROMPT + '\n\n';

    if (conversationHistory && conversationHistory.length > 0) {
        prompt += 'Conversation history:\n';
        for (const entry of conversationHistory) {
            prompt += `${entry.role}: ${entry.content}\n`;
        }
        prompt += '\n';
    }

    if (retrievedEvents && retrievedEvents.length > 0) {
        prompt += 'Relevant events from the database:\n';
        for (const event of retrievedEvents) {
            prompt += `- ID:${event.id} "${event.title}" (${event.eventType}) at ${event.location}, ${event.time || 'no time set'}, ${event.currentParticipants}/${event.maxParticipants} participants\n`;
        }
        prompt += '\nIf the user is searching for events, use FORMAT 1 with the message field describing what you found. The actual event data will be attached separately.\n\n';
    }

    prompt += `User: ${userMessage}\n\nRespond with ONLY the JSON:`;

    return prompt;
}

module.exports = { SYSTEM_PROMPT, buildSynthesisPrompt };
