# Rana AI — সেটআপ গাইড (বাংলা)

এই সংস্করণে চ্যাট UI Gemini API-তে যুক্ত করা হয়েছে। API key কখনো `index.html`, GitHub commit, বা ফোনের public code-এ রাখবেন না।

## যা যোগ হয়েছে
- `functions/api/chat.js`: Cloudflare Pages Function; Gemini key সার্ভারে রাখে।
- `index.html`: আগের ডিজাইন ও বাংলা voice-to-text রেখে `/api/chat`-এ প্রশ্ন পাঠায়।
- মডেল: `gemini-3.8-flash`।

## চালু করার জন্য প্রয়োজনীয় কাজ
এই সেটআপে Cloudflare Pages-এ GitHub repository deploy করতে হবে। Cloudflare/Gemini account, API key এবং যেকোনো billing বা usage settings একজন অভিভাবক/প্রাপ্তবয়স্কের সহায়তায় সেট আপ করুন। কোনো API key চ্যাটে বা GitHub-এ পাঠাবেন না।

1. Cloudflare Pages-এ একটি project তৈরি করে GitHub repository `hasu5284-collab/Rana-AI` সংযুক্ত করুন।
2. Framework preset: None; Build command ফাঁকা; Build output directory `.` দিন, তারপর deploy করুন।
3. Deploy শেষ হলে Pages project → Settings → Variables and Secrets → Add-এ যান।
4. Secret হিসেবে নাম দিন `GEMINI_API_KEY`; Google AI Studio থেকে পাওয়া key value সেখানে দিন; Encrypt/Secret নির্বাচন করে Save করুন।
5. নতুন deployment দিন। এরপর site খুলে একটি সাধারণ প্রশ্ন পাঠিয়ে পরীক্ষা করুন।

Cloudflare Pages Git integration: https://developers.cloudflare.com/pages/configuration/git-integration/
Cloudflare Pages secrets: https://developers.cloudflare.com/pages/functions/bindings/
Gemini API docs: https://ai.google.dev/api/generate-content

## খরচ ও নিরাপত্তা
Gemini API-এর free usage থাকলেও তা অঞ্চল, অ্যাকাউন্ট ও বর্তমান সীমার ওপর নির্ভর করে; সব ব্যবহার বিনামূল্যে হবে এমন নিশ্চয়তা নেই। API ব্যবহার শুরু করার আগে অভিভাবক/অ্যাকাউন্ট মালিককে বর্তমান মূল্য ও usage limit দেখে নিতে হবে। Secret key কাউকে পাঠাবেন না।
