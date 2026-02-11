const { GoogleGenerativeAIEmbeddings } = require('@langchain/google-genai');
const { MemoryVectorStore } = require('langchain/vectorstores/memory');
const { Document } = require('@langchain/core/documents');

let vectorStore = null;
let embeddings = null;

function getEmbeddings() {
    if (!embeddings) {
        embeddings = new GoogleGenerativeAIEmbeddings({
            apiKey: process.env.GEMINI_API_KEY,
            modelName: 'gemini-embedding-001',
        });
    }
    return embeddings;
}

function eventToText(event) {
    return `${event.title}. Type: ${event.eventType}. ${event.description || ''}. Location: ${event.location}. Time: ${event.time || 'Not specified'}.`;
}

async function initializeVectorStore(db) {
    return new Promise((resolve, reject) => {
        db.all('SELECT * FROM events', async (err, rows) => {
            if (err) {
                console.error('Error loading events for vector store:', err);
                return reject(err);
            }

            try {
                const docs = (rows || []).map(event => new Document({
                    pageContent: eventToText(event),
                    metadata: { ...event },
                }));

                if (docs.length > 0) {
                    vectorStore = await MemoryVectorStore.fromDocuments(docs, getEmbeddings());
                } else {
                    vectorStore = new MemoryVectorStore(getEmbeddings());
                }

                console.log(`Vector store initialized with ${docs.length} events`);
                resolve(vectorStore);
            } catch (e) {
                console.error('Error initializing vector store:', e);
                reject(e);
            }
        });
    });
}

async function reindexEvents(db) {
    try {
        await initializeVectorStore(db);
    } catch (e) {
        console.error('Error reindexing vector store:', e);
    }
}

async function searchEvents(query, k = 5) {
    if (!vectorStore) {
        console.warn('Vector store not initialized');
        return [];
    }

    try {
        const results = await vectorStore.similaritySearch(query, k);
        return results.map(doc => doc.metadata);
    } catch (e) {
        console.error('Error searching vector store:', e);
        return [];
    }
}

module.exports = { initializeVectorStore, reindexEvents, searchEvents };
