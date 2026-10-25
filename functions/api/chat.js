// Cloudflare Pages Function: keeps the Gemini API key on the server.
export async function onRequestPost({ request, env }) {
  const cors = { "Content-Type": "application/json; charset=utf-8" };
  if (!env.GEMINI_API_KEY) {
    return new Response(JSON.stringify({ error: "সার্ভারে Gemini API Key সেট করা হয়নি।" }), { status: 503, headers: cors });
  }

  let body;
  try { body = await request.json(); }
  catch { return new Response(JSON.stringify({ error: "সঠিক বার্তা পাঠানো হয়নি।" }), { status: 400, headers: cors }); }

  const message = typeof body.message === "string" ? body.message.trim() : "";
  if (!message || message.length > 8000) {
    return new Response(JSON.stringify({ error: "বার্তা খালি অথবা খুব বড়।" }), { status: 400, headers: cors });
  }

  const history = Array.isArray(body.history) ? body.history.slice(-12) : [];
  const contents = [];
  for (const item of history) {
    if (!item || !["user", "model"].includes(item.role) || typeof item.text !== "string") continue;
    contents.push({ role: item.role, parts: [{ text: item.text.slice(0, 4000) }] });
  }
  contents.push({ role: "user", parts: [{ text: message }] });

  const endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent";
  let upstream;
  try {
    upstream = await fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json", "x-goog-api-key": env.GEMINI_API_KEY },
      body: JSON.stringify({
        systemInstruction: { parts: [{ text: "তোমার নাম Rana AI। তুমি একজন সহায়ক, ভদ্র ও নিরাপদ বাংলা AI সহকারী। ব্যবহারকারী বাংলায় লিখলে সহজ বাংলায় উত্তর দাও। তথ্য না জানলে বানিয়ে বলবে না।" }] },
        contents,
        generationConfig: { temperature: 0.7, maxOutputTokens: 1200 }
      })
    });
  } catch {
    return new Response(JSON.stringify({ error: "AI সার্ভারের সঙ্গে যোগাযোগ করা যাচ্ছে না। পরে আবার চেষ্টা করো।" }), { status: 502, headers: cors });
  }

  const data = await upstream.json().catch(() => ({}));
  if (!upstream.ok) {
    return new Response(JSON.stringify({ error: "Gemini API অনুরোধ সফল হয়নি। সার্ভার সেটিংস ও ব্যবহারসীমা পরীক্ষা করতে হবে।" }), { status: 502, headers: cors });
  }
  const answer = data?.candidates?.[0]?.content?.parts?.map(p => p.text || "").join("").trim();
  if (!answer) {
    return new Response(JSON.stringify({ error: "এবার উত্তর পাওয়া যায়নি। অন্যভাবে প্রশ্ন করো।" }), { status: 502, headers: cors });
  }
  return new Response(JSON.stringify({ answer }), { status: 200, headers: cors });
}
