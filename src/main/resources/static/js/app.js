const app = document.getElementById("app");

let VEHICLE_CATALOG = [];

function money(v){return "₹"+Math.round(Number(v||0)).toLocaleString("en-IN");}
const CLEAN_PATH_ROUTES = new Set(["trips","find-trips","driver-register","agency-register","hotel-register","register","login","forts","spots","stays","about","contact","partner","safety","complaint","profile","dashboard","create-trip","create-property","return","how","privacy","terms","refund","trust","forgot-password","settings-password","india-nature","nature","community","trek-mates","leaderboard","offline-trails","guides","weather","ai-recommendations"]);
function initialPage(){const hash=location.hash.replace("#","");if(hash)return hash;const path=location.pathname.replace(/^\/+|\/+$/g,"");return CLEAN_PATH_ROUTES.has(path)?(path==="find-trips"?"trips":path):"home";}
const state = {
  page: initialPage(),
  search: { from: "", destination: "", date: "", passengers: 1, returnTrip: false, vehicleType: "" },
  staySearch: { location: "", checkIn: "", checkOut: "" },
  user: JSON.parse(localStorage.getItem("spt_user") || "null"),
  resetToken: null
};

function esc(v = "") { return String(v).replace(/[&<>"']/g, m => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;" }[m])); }
function toast(msg) { const el = document.createElement("div"); el.className = "toast"; el.textContent = msg; document.getElementById("toast-root").appendChild(el); setTimeout(() => el.remove(), 2600); }
function go(page) { location.hash = page; }
function applyTheme() {
  const theme = localStorage.getItem("spt_theme") || "light";
  document.documentElement.dataset.theme = theme;
  const meta = document.querySelector('meta[name="theme-color"]');
  if (meta) meta.setAttribute("content", theme === "dark" ? "#0e130f" : "#163b2a");
  const label = document.querySelector(".theme-label");
  if (label) label.textContent = theme === "dark" ? "Light" : "Dark";
}
function toggleTheme() {
  const next = document.documentElement.dataset.theme === "dark" ? "light" : "dark";
  localStorage.setItem("spt_theme", next);
  document.documentElement.dataset.theme = next;
  applyTheme();
}
applyTheme();

function loading(msg) { return `<div class="empty">${esc(msg)}</div>`; }
function emptyBlock(msg) { return `<div class="empty">${esc(msg)}</div>`; }
function errorBlock(msg) { return `<div class="empty">${esc(msg)}</div>`; }

function loginPrompt(text) {
  return `<div class="empty">${esc(text)}<br><br><button class="btn btn-primary btn-sm" onclick="go('login')">Login</button> <button class="btn btn-outline btn-sm" onclick="go('register')">Create account</button></div>`;
}

function brandLogo(compact = false) {
  return `<span class="brand-logo" aria-hidden="true"><img src="assets/mark.svg" alt="" /></span>`;
}
function header() {
  const loggedIn = !!state.user;
  return `<header class="site-header">
    <div class="utility-bar"><div class="utility-inner">
      <span class="utility-brand-note">Explore Maharashtra</span>
      <div class="utility-links">
        <a href="#contact">Contact Us</a><span class="utility-divider"></span>
        <div class="language-switcher">
          <button type="button" class="language-trigger" onclick="toggleLanguageMenu()" aria-label="Select language">
            <span id="current-language">English</span><span class="language-chevron">⌄</span>
          </button>
          <div id="language-menu" class="language-menu">
            <button type="button" onclick="setLanguage('en')">English</button>
            <button type="button" onclick="setLanguage('mr')">मराठी</button>
            <button type="button" onclick="setLanguage('hi')">हिन्दी</button>
          </div>
        </div>
      </div>
    </div></div>
    <nav class="navbar"><div class="nav-inner">
      <a class="brand" href="#home" aria-label="Sahyadri Pool & Trip home">${brandLogo()}<span class="brand-copy"><strong>Sahyadri</strong><small>POOL & TRIP</small></span></a>
      <div class="nav-links" aria-label="Primary navigation">
        <a href="#home" class="nav-home ${state.page === "home" ? "active" : ""}" data-i18n="nav.home">Home</a>
      </div>
      <div class="nav-actions">
        <button class="nav-search-trigger" type="button" onclick="openGlobalSearch()" aria-label="Search" title="Search">
          <span class="search-icon" aria-hidden="true">⌕</span>
        </button>
        ${loggedIn
        ? `<button class="btn btn-secondary btn-sm" onclick="go('dashboard')" data-i18n="nav.dashboard">Dashboard</button><button class="btn btn-primary btn-sm" onclick="logout()" data-i18n="nav.logout">Logout</button>`
        : `<button class="nav-login" onclick="go('login')" data-i18n="nav.login">Login</button>`}</div>
      <button class="settings-trigger" type="button" onclick="toggleSettingsPanel()" aria-label="Open menu and settings" title="Menu & Settings"><span></span><span></span><span></span></button>
    </div></nav>
  </header>${settingsPanel()}`;
}

function footer() { return `<footer class="footer">
  <div class="container footer-grid">
    <div class="footer-brand"><a class="brand footer-brand-link" href="#home">${brandLogo()}<span class="brand-copy"><strong>Sahyadri</strong><small>POOL & TRIP</small></span></a>
      <p>Travel together, discover Maharashtra and connect with trusted trips, stays and local experiences.</p>
      <div class="footer-contact-mini"><a href="tel:+917620815801">+91 76208 15801</a><a href="mailto:vishalshingte761@gmail.com">vishalshingte761@gmail.com</a><span>A/P Ranjani, Ambegaon<br>Pune, Maharashtra 410504</span></div>
    </div>
    <div><h3>Explore</h3><a href="#trips">Find Trips</a><a href="#forts">Maharashtra Forts</a><a href="#spots" data-i18n="menu.spots">Sahyadri Destinations</a><a href="#india-nature">Bharat Destinations</a><a href="#stays">Stays & Properties</a><a href="#return">Return Trips</a></div>
    <div><h3>Partner</h3><a href="#partner">Partner With Us</a><a href="#driver-register">Driver Registration</a><a href="#agency-register">Agency Registration</a><a href="#hotel-register">Hotel Registration</a></div>
    <div><h3>Support</h3><a href="#contact">Contact Us</a><a href="#safety">Safety & SOS</a><a href="#complaint">Help & Complaint</a><a href="#about">About Us</a></div>
  </div>
  <div class="container footer-bottom"><span>© 2026 Sahyadri Pool & Trip. All rights reserved.</span><span>Explore Maharashtra. Travel Together.</span></div>
</footer>`; }

function infoPage(title, eyebrow, content) { return `${header()}<main class="page"><div class="page-header"><div class="container"><span class="eyebrow page-eyebrow">${eyebrow}</span><h1>${title}</h1><p class="section-sub">Sahyadri Pool & Trip — Travel Together. Explore More.</p></div></div><section class="section"><div class="container"><article class="legal-card">${content}</article></div></section></main>${footer()}`; }
function about() { return infoPage("About Us", "ABOUT SAHYADRI", `<h2>Travel that connects people, places and possibilities.</h2><p>Sahyadri Pool & Trip is a Maharashtra-focused travel platform designed to bring shared trips, local stays and travel partners into one simple experience.</p><p>Our goal is to make exploring forts, hills, trails and destinations easier by helping travelers discover available rides and stays while giving verified partners a practical way to publish their services.</p><div class="info-grid"><div><span class="info-kicker">Founder & Owner</span><strong>Vishal Machhindra Shingte</strong></div><div><span class="info-kicker">Location</span><strong>A/P Ranjani, Ambegaon 410504, Pune - Maharashtra</strong></div><div><span class="info-kicker">Contact</span><strong>7620815801</strong></div><div><span class="info-kicker">Email</span><strong>vishalshingte761@gmail.com</strong></div></div><p class="note-box">We do not claim government certification, third-party accreditation or partner status unless it is actually verified and displayed on the platform.</p>`); }
function privacy() { return infoPage("Privacy Policy", "YOUR PRIVACY", `<h2>We collect only what is needed to operate the platform.</h2><h3>Information we may collect</h3><p>Account details, contact information, booking information, partner verification documents, complaint attachments and safety information may be processed when you use the relevant features.</p><h3>How information is used</h3><p>We use information to authenticate accounts, process bookings, support trips and stays, verify partners, handle complaints, maintain platform safety and provide requested services.</p><h3>Documents and sensitive operational data</h3><p>Partner verification documents are intended for authorized verification workflows. Access should be restricted to the roles that require it.</p><h3>Payments</h3><p>Payment processing is handled through the payment provider integrated with the platform. We do not ask users to share card, UPI PIN or banking passwords through ordinary website forms.</p><h3>Contact</h3><p>For privacy questions, contact <a href="mailto:vishalshingte761@gmail.com">vishalshingte761@gmail.com</a>.</p>`); }
function terms() { return infoPage("Terms & Conditions", "PLATFORM TERMS", `<h2>Use Sahyadri Pool & Trip responsibly.</h2><h3>Accounts</h3><p>Users must provide accurate information and keep account credentials secure. Roles with verification requirements may be subject to admin review before access to partner functions is enabled.</p><h3>Trips and stays</h3><p>Availability, pricing, seats, rooms and booking status are based on platform data. Users should review the booking details before confirming.</p><h3>Partner responsibilities</h3><p>Drivers, agencies and property partners are responsible for truthful listings, valid documents, lawful operations and accurate service information.</p><h3>Safety</h3><p>Emergency and SOS tools are support features and should not replace local emergency services. Use them only when appropriate and keep emergency contact information updated.</p><h3>Prohibited use</h3><p>Fraud, impersonation, abusive behavior, misleading listings, document misuse and attempts to bypass platform security are prohibited.</p>`); }
function refund() { return infoPage("Cancellation & Refund Policy", "BOOKING POLICY", `<h2>Clear booking and cancellation expectations.</h2><h3>Trip bookings</h3><p>Cancellation and refund eligibility depends on the booking status, the applicable trip policy and the payment transaction status. A cancelled booking does not automatically mean a cash refund is due in every case.</p><h3>Property bookings</h3><p>Property booking cancellations are subject to the applicable property and platform cancellation rules presented at the time of booking.</p><h3>Payment issues</h3><p>If a payment is debited but the booking is not confirmed, users should contact support with the booking/payment reference so the transaction can be checked.</p><h3>How to request help</h3><p>Submit a complaint through the platform or contact <a href="mailto:vishalshingte761@gmail.com">vishalshingte761@gmail.com</a> with the relevant booking details.</p><p class="note-box">The final production refund rules should be kept consistent with the exact Razorpay/payment integration and the booking policy shown at checkout.</p>`); }
function trust() { return infoPage("Trust & Partners", "TRUST CENTER", `<h2>Built around verification, transparency and safer travel.</h2><div class="trust-grid"><div><strong>Verified Partner Workflow</strong><p>Driver and agency onboarding can include document verification and admin approval before partner functionality is enabled.</p></div><div><strong>Real Platform Data</strong><p>Trips and properties are fetched from the backend rather than presented as decorative mock inventory.</p></div><div><strong>Safety Support</strong><p>The platform includes emergency contact and SOS workflows for authenticated users.</p></div><div><strong>Secure Payments</strong><p>Payment processing is designed to use the configured payment gateway rather than collecting card credentials directly.</p></div></div><div class="partner-owner"><span class="info-kicker">Platform Owner</span><strong>Vishal Machhindra Shingte</strong></div>`); }

// =====================================================
// HOME
// =====================================================
function home() {
  const homeDestinations = [
    { id:"MH-HOME-01", name:"Rajgad Fort", region:"Pune", type:"Fort & Trek", price:1200 },
    { id:"MH-HOME-02", name:"Kalsubai Peak", region:"Nashik", type:"Peak & Trek", price:1500 },
    { id:"MH-HOME-03", name:"Tarkarli Beach", region:"Sindhudurg", type:"Beach Escape", price:1700 }
  ];
  const cards = homeDestinations.map(s => `<div class="card home-destination-card"><div class="place-photo live-home-photo" data-home-photo="${esc(s.name)}" data-home-id="${esc(s.id)}"><span>🌄</span><small>Loading destination photo…</small></div><div class="card-body"><span class="tag">${esc(s.type)}</span><h3>${esc(s.name)}</h3><p class="muted">${esc(s.region)}</p><div style="display:flex;justify-content:space-between;align-items:center;gap:12px"><span class="price">₹${s.price}</span><button class="btn btn-secondary btn-sm" onclick="searchSpot('${esc(s.name)}')">Find trips</button></div></div></div>`).join("");
  return `${header()}<main>
  <section class="hero"><div class="container hero-content"><span class="eyebrow">Adventure • Pooling • Stay • Food</span><h1>Travel the Sahyadri.<br>Share the journey.</h1><p>Ek jagah jahan forts, offbeat destinations, seat pooling, custom pickups, local stays aur return rides ek smooth experience mein milte hain.</p>
  <form class="search-card" onsubmit="homeSearch(event)"><div class="field"><label>WHERE TO?</label><input id="home-dest" placeholder="Rajgad, Lonavala, Kalsubai..." /></div><div class="field"><label>DATE</label><input id="home-date" type="date" /></div><div class="field"><label>TRAVELERS</label><input id="home-pass" type="number" min="1" value="1" /></div><button class="btn btn-primary" type="submit">Search Trips</button></form></div></section>
  <section class="section"><div class="container"><div class="section-head"><div><h2>Popular Sahyadri escapes</h2><p class="section-sub">Pick a destination and discover available shared rides.</p></div><button class="btn btn-outline" onclick="go('trips')">View all trips</button></div><div class="grid grid-3">${cards}</div></div></section>
  <section class="section dark-section"><div class="container"><div class="section-head"><div><h2>Everything for the trip</h2><p class="section-sub">From your first pickup to your return ride.</p></div></div><div class="grid grid-4">
  ${feature("01", "Seat Pooling", "Share available seats and travel at a better price.")}
  ${feature("02", "Live Trips", "Real trips published by drivers, pulled straight from the database.")}
  ${feature("03", "Local Stays", "Hotels and homestays with live room availability.")}
  ${feature("04", "Return Trips", "Find a ride back and reduce empty-return loss.")}
  </div></div></section>
  <section class="section ecosystem-section"><div class="container"><div class="section-head"><div><span class="eyebrow">SAHYADRI ECOSYSTEM</span><h2>Everything you need, without the clutter.</h2><p class="section-sub">मुख्य सुविधा इथेच. बाकी tools योग्य ठिकाणी menu आणि संबंधित pages मधून मिळतील.</p></div></div><div class="ecosystem-grid">
    ${ecosystemCard("🧭","Explore","Forts, Sahyadri destinations, routes and Bharat travel.","forts","Explore Places","ecosystem-explore")}
    ${ecosystemCard("🚐","Travel","Shared trips, pool rides and return journeys.","trips","Find a Trip","ecosystem-travel")}
    ${ecosystemCard("🏡","Stay","Hotels, homestays and verified local stays.","stays","Find a Stay","ecosystem-stay")}
    ${ecosystemCard("👥","Community","Trek mates, traveller updates, photos and badges.","community","Join Community","ecosystem-community")}
    ${ecosystemCard("🛡️","Safety","SOS, emergency help, GPX trails and safety tools.","safety","Open Safety","ecosystem-safety")}
    ${ecosystemCard("🤖","Smart","AI trek suggestions and live weather insights.","ai-recommendations","Explore Smart","ecosystem-smart")}
  </div></div></section>
  <section class="section community-preview"><div class="container"><div class="community-preview-card"><div><span class="eyebrow">NEW</span><h2>Plan smarter. Trek safer. Travel together.</h2><p>Find people for your next trek, check fresh trail updates, download routes for offline use and discover stays near the base village.</p></div><div class="community-preview-actions"><button class="btn btn-primary" onclick="go('community')">Explore Community</button><button class="btn btn-outline" onclick="go('offline-trails')">Offline Trails</button></div></div></div></section>
  <section class="section" id="how"><div class="container"><div class="section-head"><div><h2>Built for every role</h2><p class="section-sub">One account system, different dashboards.</p></div></div><div class="role-grid">
  ${roleCard("TR", "Traveler", "Search, book, stay, eat and return.", "register", "TRAVELER")}
  ${roleCard("DR", "Driver", "Verified drivers can publish shared trips.", "driver-register", "DRIVER")} ${roleCard("AG", "Travel Agency", "Register your agency with mandatory document verification.", "agency-register", "AGENCY")}
  ${roleCard("ST", "Stay Owner", "Admin-approved onboarding for verified property partners.", "hotel-register", "HOTEL_OWNER")}
  </div></div></section></main>${footer()}`;
}
function feature(i, t, d) { return `<div class="feature-card"><div class="icon-box">${i}</div><h3>${t}</h3><p style="color:#c8d4cc;line-height:1.6;font-size:13px">${d}</p></div>` }
function ecosystemCard(icon,title,desc,route,cta,cls=""){return `<article class="ecosystem-card ${cls}"><div class="ecosystem-icon">${icon}</div><div><h3>${title}</h3><p>${desc}</p></div><button class="btn btn-outline btn-sm" onclick="go('${route}')">${cta} <span aria-hidden="true">→</span></button></article>`}
function roleCard(i, t, d, p, r) { return `<article class="role-card"><div class="role-icon">${i}</div><h3>${t}</h3><p>${d}</p><a class="btn btn-secondary btn-sm role-continue" href="#${p}" onclick="event.preventDefault(); roleGo('${p}','${r}');">Continue</a></article>` }
function roleGo(page, role) { if(role) sessionStorage.setItem("spt_role", role); if(location.hash !== `#${page}`) location.hash = page; else render(); }

// =====================================================
// AUTH — REGISTER / LOGIN (real backend calls)
// =====================================================
function register() {
  const selected = sessionStorage.getItem("spt_role") || "TRAVELER";
  return `${header()}<main class="auth-wrap"><div class="auth-card"><h1>Create your account</h1><p class="muted">Choose your role. Every account uses the same login form.</p><form onsubmit="registerSubmit(event)">
  <div class="field full"><label>ACCOUNT TYPE</label><div class="role-select">${roleOption("TRAVELER", "Traveler", "TRAVELER", "TRAVELER")}</div><p class="form-hint">Stay Owner accounts use admin-approved onboarding and cannot be created through public registration.</p></div>
  <div class="form-grid" style="margin-top:15px"><div class="field"><label>FULL NAME</label><input id="reg-name" required placeholder="Your name"></div><div class="field"><label>PHONE</label><input id="reg-phone" inputmode="numeric" pattern="[6-9][0-9]{9}" maxlength="10" required placeholder="10-digit mobile number"></div><div class="field"><label>EMAIL</label><input id="reg-email" type="email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}" required placeholder="you@example.com"></div><div class="field"><label>PASSWORD</label><input id="reg-pass" type="password" required minlength="8" placeholder="Minimum 8 characters"></div></div>
  <p class="muted" id="dynamic-note" style="margin-top:14px"></p><div style="display:flex;gap:10px;flex-wrap:wrap;margin-top:12px"><a class="btn btn-outline btn-sm" href="#driver-register" onclick="roleGo('driver-register','DRIVER')">Driver Registration</a><a class="btn btn-outline btn-sm" href="#agency-register" onclick="roleGo('agency-register','AGENCY')">Travel Agency Registration</a></div>
  <label class="check" style="margin:16px 0"><input type="checkbox" required> I agree to platform terms and verification.</label><button class="btn btn-primary" style="width:100%" id="reg-submit-btn">Create Account</button></form><p class="muted" style="text-align:center;margin-bottom:0">Already registered? <a href="#login">Login</a></p></div></main>`;
}
function roleOption(id, text, val, selected) { return `<label><input type="radio" name="role" value="${val}" ${selected === val ? "checked" : ""} onchange="updateRegFields()">${text}</label>` }
function updateRegFields() {
  const role = document.querySelector('input[name="role"]:checked')?.value || "TRAVELER";
  const el = document.getElementById("dynamic-note");
  if (!el) return;
  const notes = {
    TRAVELER: "You'll be able to search trips, book seats, book stays and leave reviews.",
    DRIVER: "After registering, log in and publish your first trip from the Driver Dashboard.",
    HOTEL_OWNER: "After registering, log in and list your first property from the Stay Owner Dashboard."
  };
  el.textContent = notes[role] || "";
}

async function registerSubmit(e) {
  e.preventDefault();

  const name = document.getElementById("reg-name").value.trim();
  const email = document.getElementById("reg-email").value.trim();
  const phone = document.getElementById("reg-phone").value.trim();
  const password = document.getElementById("reg-pass").value;
  const role = document.querySelector('input[name="role"]:checked')?.value || "TRAVELER";
  const btn = document.getElementById("reg-submit-btn");

  try {
    if (btn) { btn.disabled = true; btn.textContent = "Creating account..."; }

    const response = await API.post("/api/auth/register", { name, email, phone, password, role });

    toast(response.message || "Account created successfully.");
    sessionStorage.removeItem("spt_role");
    go("login");

  } catch (error) {
    console.error("Registration error:", error);
    toast(error.message || "Registration failed.");
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = "Create Account"; }
  }
}

function login() { return `${header()}<main class="auth-wrap"><div class="auth-card"><h1>Welcome back</h1><p class="muted">One login for every role — traveler, driver, stay owner or admin.</p><form onsubmit="loginSubmit(event)"><div class="field"><label>EMAIL</label><input id="login-id" type="email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}" required placeholder="you@example.com"></div><div class="field" style="margin-top:14px"><label>PASSWORD</label><input id="login-pass" type="password" required placeholder="••••••••"></div><div style="display:flex;justify-content:space-between;align-items:center;margin:14px 0"><label class="check"><input type="checkbox"> Remember me</label><a href="#forgot-password">Forgot password?</a></div><button class="btn btn-primary" style="width:100%" id="login-submit-btn">Login</button></form><p class="muted" style="text-align:center;margin-bottom:0">New here? <a href="#register">Create an account</a></p></div></main>`; }

async function loginSubmit(e) {
  e.preventDefault();

  const email = document.getElementById("login-id").value.trim();
  if (!/^[a-z0-9][a-z0-9._%+-]*@[a-z0-9.-]+\.[a-z]{2,}$/.test(email)) { toast("Use a valid lowercase email address."); return; }
  const password = document.getElementById("login-pass").value;
  const btn = document.getElementById("login-submit-btn");

  try {
    if (btn) { btn.disabled = true; btn.textContent = "Logging in..."; }

    const response = await API.post("/api/auth/login", { email, password });

    localStorage.setItem("spt_token", response.token);

    const user = {
      userId: response.userId,
      name: response.name,
      email: response.email,
      role: response.role
    };

    localStorage.setItem("spt_user", JSON.stringify(user));
    state.user = user;

    toast(response.message || "Login successful.");
    sessionStorage.removeItem("spt_role");
    go("dashboard");

  } catch (error) {
    console.error("Login error:", error);
    toast(error.message || "Login failed.");
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = "Login"; }
  }
}

function forgotPassword(){
  return `${header()}<main class="auth-wrap"><div class="auth-card" style="max-width:620px"><h1>Reset your password</h1><p class="muted">Choose email or phone. An OTP will be sent to your registered contact.</p><div class="role-select" style="margin:16px 0"><label><input type="radio" name="reset-channel" value="EMAIL" checked onchange="toggleResetChannel()"> Email</label><label><input type="radio" name="reset-channel" value="PHONE" onchange="toggleResetChannel()"> Phone</label></div><form onsubmit="startPasswordReset(event)"><div class="field"><label id="reset-label">EMAIL</label><input id="reset-identifier" required placeholder="you@example.com"></div><button class="btn btn-primary" style="width:100%;margin-top:14px" id="reset-send">Send OTP</button></form><div id="reset-verify" style="display:none;margin-top:20px"><div class="field"><label>OTP</label><input id="reset-otp" inputmode="numeric" maxlength="6" placeholder="6-digit OTP"></div><button class="btn btn-secondary" style="width:100%;margin-top:12px" onclick="verifyPasswordReset()">Verify OTP</button></div><div id="reset-new" style="display:none;margin-top:20px"><div class="field"><label>NEW PASSWORD</label><input id="reset-password" type="password" minlength="8" required></div><button class="btn btn-primary" style="width:100%;margin-top:12px" onclick="confirmPasswordReset()">Set New Password</button></div><p class="muted" style="text-align:center;margin-top:18px"><a href="#login">Back to login</a></p></div></main>`;
}
function toggleResetChannel(){const ch=document.querySelector('input[name="reset-channel"]:checked')?.value||'EMAIL';const label=document.getElementById('reset-label');const input=document.getElementById('reset-identifier');if(label)label.textContent=ch==='EMAIL'?'EMAIL':'PHONE';if(input){input.value='';input.placeholder=ch==='EMAIL'?'you@example.com':'10-digit mobile number';input.type=ch==='EMAIL'?'email':'tel';input.pattern=ch==='EMAIL'?'[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}':'[6-9][0-9]{9}';}}
async function startPasswordReset(e){e.preventDefault();const ch=document.querySelector('input[name="reset-channel"]:checked')?.value||'EMAIL';const id=document.getElementById('reset-identifier').value.trim();try{if(ch==='EMAIL'&&!/^[a-z0-9][a-z0-9._%+-]*@[a-z0-9.-]+\.[a-z]{2,}$/.test(id))throw new Error('Use lowercase email only');if(ch==='PHONE'&&!/^[6-9]\d{9}$/.test(id))throw new Error('Phone number must be exactly 10 digits');await API.post('/api/auth/password/forgot/'+ch.toLowerCase(),{identifier:id});document.getElementById('reset-verify').style.display='block';toast('OTP sent successfully.');}catch(err){toast(err.message)}}
async function verifyPasswordReset(){const id=document.getElementById('reset-identifier').value.trim();const otp=document.getElementById('reset-otp').value.trim();try{const r=await API.post('/api/auth/password/verify',{identifier:id,otp});state.resetToken=r.resetToken;document.getElementById('reset-new').style.display='block';toast('OTP verified. Set your new password.');}catch(err){toast(err.message)}}
async function confirmPasswordReset(){const pass=document.getElementById('reset-password').value;if(pass.length<8){toast('Password must contain at least 8 characters');return}try{await API.post('/api/auth/password/reset',{resetToken:state.resetToken,newPassword:pass});toast('Password reset successful.');go('login')}catch(err){toast(err.message)}}

function logout() {
  localStorage.removeItem("spt_token");
  localStorage.removeItem("spt_user");
  state.user = null;
  toast("Logged out.");
  go("home");
}
const SPT_TRANSLATIONS = {
  en: {"nav.home":"Home","nav.trips":"Find Trips","nav.forts":"Forts","nav.spots":"Sahyadri","nav.stays":"Stays","nav.how":"How It Works","nav.about":"About","nav.login":"Login","nav.dashboard":"Dashboard","nav.logout":"Logout",
       "menu.home":"Home","menu.trips":"Find Trips","menu.forts":"Maharashtra Forts","menu.spots":"Sahyadri Destinations","menu.stays":"Stays & Properties","menu.return":"Return Trips","menu.about":"About Us","menu.contact":"Contact Us","menu.profile":"Profile","menu.safety":"Safety & SOS","menu.complaint":"Help & Complaint","menu.partner":"Partner With Us","menu.privacy":"Privacy Policy","menu.terms":"Terms & Conditions","menu.refund":"Cancellation & Refund","menu.trust":"Trust & Partners","menu.light":"Light Mode","menu.dark":"Dark Mode","menu.title":"Menu & Settings"},
  mr: {"nav.home":"मुख्यपृष्ठ","nav.trips":"प्रवास शोधा","nav.forts":"किल्ले","nav.spots":"सह्याद्री","nav.stays":"निवास","nav.how":"कसे कार्य करते","nav.about":"आमच्याबद्दल","nav.login":"लॉगिन","nav.dashboard":"डॅशबोर्ड","nav.logout":"लॉगआउट",
       "menu.home":"मुख्यपृष्ठ","menu.trips":"प्रवास शोधा","menu.forts":"महाराष्ट्रातील किल्ले","menu.spots":"सह्याद्री डेस्टिनेशन्स","menu.stays":"निवास आणि मालमत्ता","menu.return":"परतीचे प्रवास","menu.about":"आमच्याबद्दल","menu.contact":"संपर्क करा","menu.profile":"प्रोफाइल","menu.safety":"सुरक्षितता आणि SOS","menu.complaint":"मदत आणि तक्रार","menu.partner":"भागीदार व्हा","menu.privacy":"गोपनीयता धोरण","menu.terms":"अटी आणि शर्ती","menu.refund":"रद्दीकरण आणि परतावा","menu.trust":"विश्वास आणि भागीदार","menu.light":"लाईट मोड","menu.dark":"डार्क मोड","menu.title":"मेनू आणि सेटिंग्ज"},
  hi: {"nav.home":"होम","nav.trips":"यात्राएँ खोजें","nav.forts":"किले","nav.spots":"सह्याद्री","nav.stays":"ठहरने की जगह","nav.how":"यह कैसे काम करता है","nav.about":"हमारे बारे में","nav.login":"लॉगिन","nav.dashboard":"डैशबोर्ड","nav.logout":"लॉगआउट",
       "menu.home":"होम","menu.trips":"यात्राएँ खोजें","menu.forts":"महाराष्ट्र के किले","menu.spots":"सह्याद्री डेस्टिनेशन्स","menu.stays":"ठहरने की जगह और प्रॉपर्टीज","menu.return":"वापसी यात्राएँ","menu.about":"हमारे बारे में","menu.contact":"संपर्क करें","menu.profile":"प्रोफाइल","menu.safety":"सुरक्षा और SOS","menu.complaint":"मदद और शिकायत","menu.partner":"पार्टनर बनें","menu.privacy":"गोपनीयता नीति","menu.terms":"नियम और शर्तें","menu.refund":"रद्दीकरण और रिफंड","menu.trust":"विश्वास और पार्टनर्स","menu.light":"लाइट मोड","menu.dark":"डार्क मोड","menu.title":"मेनू और सेटिंग्स"}
};
const SITE_TEXT_I18N = {
  en:{
    "Contact Us":"Contact Us","Explore Maharashtra":"Explore Maharashtra","Menu & Settings":"Menu & Settings","QUICK NAVIGATION":"QUICK NAVIGATION","ACCOUNT & SAFETY":"ACCOUNT & SAFETY","LEGAL & INFORMATION":"LEGAL & INFORMATION","APPEARANCE":"APPEARANCE","Light Mode":"Light Mode","Dark Mode":"Dark Mode","Profile":"Profile","Change Password":"Change Password","Safety & SOS":"Safety & SOS","Help & Complaint":"Help & Complaint","Partner With Us":"Partner With Us","Privacy Policy":"Privacy Policy","Terms & Conditions":"Terms & Conditions","Cancellation & Refund":"Cancellation & Refund","Trust & Partners":"Trust & Partners",
    "About Us":"About Us","Travel that connects people, places and possibilities.":"Travel that connects people, places and possibilities.","Sahyadri Pool & Trip is a Maharashtra-focused travel platform designed to bring shared trips, local stays and travel partners into one simple experience.":"Sahyadri Pool & Trip is a Maharashtra-focused travel platform designed to bring shared trips, local stays and travel partners into one simple experience.","Our goal is to make exploring forts, hills, trails and destinations easier by helping travelers discover available rides and stays while giving verified partners a practical way to publish their services.":"Our goal is to make exploring forts, hills, trails and destinations easier by helping travelers discover available rides and stays while giving verified partners a practical way to publish their services.",
    "Find your next trip":"Find your next trip","Find Trips":"Find Trips","Stays":"Stays","How It Works":"How It Works","Dashboard":"Dashboard","Logout":"Logout","Login":"Login","Search":"Search","Forgot password?":"Forgot password?","Create an account":"Create an account","Remember me":"Remember me","Welcome back":"Welcome back","Create a new trip":"Create a new trip","List a new property":"List a new property","Profile & Account":"Profile & Account","Personal details":"Personal details","Emergency contact":"Emergency contact","Safety & SOS":"Safety & SOS","Need urgent help?":"Need urgent help?","Safety checklist":"Safety checklist","Need help, not an emergency?":"Need help, not an emergency?","Change Password":"Change Password","Keep your account secure":"Keep your account secure","Update Password":"Update Password"
  },
  mr:{
    "Contact Us":"संपर्क करा","Explore Maharashtra":"महाराष्ट्र एक्सप्लोर करा","Menu & Settings":"मेनू आणि सेटिंग्ज","QUICK NAVIGATION":"जलद नेव्हिगेशन","ACCOUNT & SAFETY":"खाते आणि सुरक्षितता","LEGAL & INFORMATION":"कायदेशीर आणि माहिती","APPEARANCE":"दिसणे","Light Mode":"लाईट मोड","Dark Mode":"डार्क मोड","Profile":"प्रोफाइल","Change Password":"पासवर्ड बदला","Safety & SOS":"सुरक्षितता आणि SOS","Help & Complaint":"मदत आणि तक्रार","Partner With Us":"भागीदार व्हा","Privacy Policy":"गोपनीयता धोरण","Terms & Conditions":"अटी आणि शर्ती","Cancellation & Refund":"रद्दीकरण आणि परतावा","Trust & Partners":"विश्वास आणि भागीदार",
    "About Us":"आमच्याबद्दल","Travel that connects people, places and possibilities.":"लोक, ठिकाणे आणि शक्यता जोडणारा प्रवास.","Sahyadri Pool & Trip is a Maharashtra-focused travel platform designed to bring shared trips, local stays and travel partners into one simple experience.":"Sahyadri Pool & Trip हे महाराष्ट्र-केंद्रित प्रवास व्यासपीठ आहे, जे सामायिक प्रवास, स्थानिक निवास आणि प्रवास भागीदारांना एका सोप्या अनुभवात आणते.","Our goal is to make exploring forts, hills, trails and destinations easier by helping travelers discover available rides and stays while giving verified partners a practical way to publish their services.":"प्रवाशांना उपलब्ध प्रवास आणि निवास शोधता यावेत आणि सत्यापित भागीदारांना त्यांच्या सेवा प्रकाशित करता याव्यात, यामुळे किल्ले, डोंगर, ट्रेल्स आणि ठिकाणे शोधणे सोपे करणे हे आमचे ध्येय आहे.",
    "Find your next trip":"तुमचा पुढचा प्रवास शोधा","Find Trips":"प्रवास शोधा","Stays":"निवास","How It Works":"कसे कार्य करते","Dashboard":"डॅशबोर्ड","Logout":"लॉगआउट","Login":"लॉगिन","Search":"शोधा","Forgot password?":"पासवर्ड विसरलात?","Create an account":"खाते तयार करा","Remember me":"मला लक्षात ठेवा","Welcome back":"पुन्हा स्वागत आहे","Create a new trip":"नवीन प्रवास तयार करा","List a new property":"नवीन मालमत्ता नोंदवा","Profile & Account":"प्रोफाइल आणि खाते","Personal details":"वैयक्तिक माहिती","Emergency contact":"आपत्कालीन संपर्क","Need urgent help?":"तातडीची मदत हवी आहे?","Safety checklist":"सुरक्षितता तपासणी यादी","Need help, not an emergency?":"मदत हवी आहे, पण आपत्कालीन स्थिती नाही?","Keep your account secure":"तुमचे खाते सुरक्षित ठेवा","Update Password":"पासवर्ड अपडेट करा"
  },
  hi:{
    "Contact Us":"संपर्क करें","Explore Maharashtra":"महाराष्ट्र देखें","Menu & Settings":"मेनू और सेटिंग्स","QUICK NAVIGATION":"त्वरित नेविगेशन","ACCOUNT & SAFETY":"खाता और सुरक्षा","LEGAL & INFORMATION":"कानूनी और जानकारी","APPEARANCE":"दिखावट","Light Mode":"लाइट मोड","Dark Mode":"डार्क मोड","Profile":"प्रोफाइल","Change Password":"पासवर्ड बदलें","Safety & SOS":"सुरक्षा और SOS","Help & Complaint":"मदद और शिकायत","Partner With Us":"पार्टनर बनें","Privacy Policy":"गोपनीयता नीति","Terms & Conditions":"नियम और शर्तें","Cancellation & Refund":"रद्दीकरण और रिफंड","Trust & Partners":"विश्वास और पार्टनर्स",
    "About Us":"हमारे बारे में","Travel that connects people, places and possibilities.":"लोगों, स्थानों और संभावनाओं को जोड़ने वाला सफर.","Sahyadri Pool & Trip is a Maharashtra-focused travel platform designed to bring shared trips, local stays and travel partners into one simple experience.":"Sahyadri Pool & Trip महाराष्ट्र-केंद्रित यात्रा प्लेटफॉर्म है, जो साझा यात्राओं, स्थानीय ठहरने की जगहों और यात्रा भागीदारों को एक सरल अनुभव में लाता है।","Our goal is to make exploring forts, hills, trails and destinations easier by helping travelers discover available rides and stays while giving verified partners a practical way to publish their services.":"हमारा उद्देश्य यात्रियों को उपलब्ध सवारी और ठहरने की जगहें खोजने में मदद करके किले, पहाड़, ट्रेल और गंतव्य देखना आसान बनाना है, साथ ही सत्यापित पार्टनर्स को अपनी सेवाएँ प्रकाशित करने का आसान तरीका देना है।",
    "Find your next trip":"अपनी अगली यात्रा खोजें","Find Trips":"यात्राएँ खोजें","Stays":"ठहरने की जगह","How It Works":"यह कैसे काम करता है","Dashboard":"डैशबोर्ड","Logout":"लॉगआउट","Login":"लॉगिन","Search":"खोजें","Forgot password?":"पासवर्ड भूल गए?","Create an account":"खाता बनाएं","Remember me":"मुझे याद रखें","Welcome back":"वापसी पर स्वागत है","Create a new trip":"नई यात्रा बनाएं","List a new property":"नई प्रॉपर्टी जोड़ें","Profile & Account":"प्रोफाइल और खाता","Personal details":"व्यक्तिगत जानकारी","Emergency contact":"आपातकालीन संपर्क","Need urgent help?":"तुरंत मदद चाहिए?","Safety checklist":"सुरक्षा चेकलिस्ट","Need help, not an emergency?":"मदद चाहिए, लेकिन आपातकाल नहीं है?","Keep your account secure":"अपना खाता सुरक्षित रखें","Update Password":"पासवर्ड अपडेट करें"
  }
};
Object.assign(SITE_TEXT_I18N.en, {
  "PARTNER WITH SAHYADRI":"PARTNER WITH SAHYADRI","Grow your travel business with Sahyadri":"Grow your travel business with Sahyadri","Choose the partner path that matches your service. Verification keeps the platform reliable for travelers.":"Choose the partner path that matches your service. Verification keeps the platform reliable for travelers.","TRANSPORT":"TRANSPORT","Driver Partner":"Driver Partner","Publish shared trips, manage seats and serve travelers across Maharashtra.":"Publish shared trips, manage seats and serve travelers across Maharashtra.","Driver Registration":"Driver Registration","BUSINESS":"BUSINESS","Travel Agency":"Travel Agency","Submit your agency details and required documents for admin verification.":"Submit your agency details and required documents for admin verification.","Agency Registration":"Agency Registration","STAYS":"STAYS","Hotel / Stay Owner":"Hotel / Stay Owner","Register your hotel or homestay for an admin-reviewed stay partner account.":"Register your hotel or homestay for an admin-reviewed stay partner account.","Hotel Registration":"Hotel Registration","Professional onboarding":"Professional onboarding","Partner applications are reviewed before partner-only features are enabled.":"Partner applications are reviewed before partner-only features are enabled.",
  "GET IN TOUCH":"GET IN TOUCH","Contact Sahyadri Pool & Trip":"Contact Sahyadri Pool & Trip","Questions, booking support, partnerships or feedback — send us a message directly from the website.":"Questions, booking support, partnerships or feedback — send us a message directly from the website.","SAHYADRI POOL & TRIP":"SAHYADRI POOL & TRIP","We’re here to help.":"We’re here to help.","For support, partnership enquiries and general questions, use the form. Your message is submitted to the platform support inbox.":"For support, partnership enquiries and general questions, use the form. Your message is submitted to the platform support inbox.","PHONE":"PHONE","EMAIL":"EMAIL","OFFICE / BASE":"OFFICE / BASE","SUPPORT HOURS":"SUPPORT HOURS","SEND A MESSAGE":"SEND A MESSAGE","How can we help?":"How can we help?","We’ll receive your message directly. No email application is required.":"We’ll receive your message directly. No email application is required.","NAME *":"NAME *","SUBJECT *":"SUBJECT *","MESSAGE *":"MESSAGE *","Your full name":"Your full name","Send Message":"Send Message","Your message is sent securely to the Sahyadri support inbox through the website backend.":"Your message is sent securely to the Sahyadri support inbox through the website backend.",
  "STAY PARTNER ONBOARDING":"STAY PARTNER ONBOARDING","Hotel & Homestay Registration":"Hotel & Homestay Registration","Submit your property and owner details. Admin approval is required before a Stay Owner account can publish properties.":"Submit your property and owner details. Admin approval is required before a Stay Owner account can publish properties.","OWNER DETAILS":"OWNER DETAILS","Your account":"Your account","OWNER / CONTACT NAME *":"OWNER / CONTACT NAME *","MOBILE *":"MOBILE *","PASSWORD *":"PASSWORD *","PROPERTY DETAILS":"PROPERTY DETAILS","Tell us about your stay":"Tell us about your stay","PROPERTY / HOTEL NAME *":"PROPERTY / HOTEL NAME *","PROPERTY TYPE *":"PROPERTY TYPE *","ADDRESS *":"ADDRESS *","CITY / DISTRICT *":"CITY / DISTRICT *","TOTAL ROOMS *":"TOTAL ROOMS *","STARTING PRICE / NIGHT (₹) *":"STARTING PRICE / NIGHT (₹) *","PROPERTY DESCRIPTION *":"PROPERTY DESCRIPTION *","I confirm that the information is accurate and I agree to admin verification before activation.":"I confirm that the information is accurate and I agree to admin verification before activation.","Submit Registration":"Submit Registration"
});
Object.assign(SITE_TEXT_I18N.mr, {
  "PARTNER WITH SAHYADRI":"सह्याद्रीसोबत भागीदारी","Grow your travel business with Sahyadri":"तुमचा प्रवास व्यवसाय सह्याद्रीसोबत वाढवा","Choose the partner path that matches your service. Verification keeps the platform reliable for travelers.":"तुमच्या सेवेला योग्य भागीदारी निवडा. पडताळणीमुळे प्रवाशांसाठी व्यासपीठ अधिक विश्वासार्ह राहते.","TRANSPORT":"वाहतूक","Driver Partner":"ड्रायव्हर भागीदार","Publish shared trips, manage seats and serve travelers across Maharashtra.":"सामायिक प्रवास प्रकाशित करा, सीट्स व्यवस्थापित करा आणि महाराष्ट्रभर प्रवाशांना सेवा द्या.","Driver Registration":"ड्रायव्हर नोंदणी","BUSINESS":"व्यवसाय","Travel Agency":"प्रवास संस्था","Submit your agency details and required documents for admin verification.":"तुमच्या संस्थेची माहिती आणि आवश्यक कागदपत्रे प्रशासकीय पडताळणीसाठी सादर करा.","Agency Registration":"संस्था नोंदणी","STAYS":"निवास","Hotel / Stay Owner":"हॉटेल / निवास मालक","Register your hotel or homestay for an admin-reviewed stay partner account.":"प्रशासकीय पडताळणीसाठी तुमचे हॉटेल किंवा होमस्टे नोंदवा.","Hotel Registration":"हॉटेल नोंदणी","Professional onboarding":"व्यावसायिक नोंदणी","Partner applications are reviewed before partner-only features are enabled.":"भागीदारांसाठीची सुविधा सुरू करण्यापूर्वी अर्जांची पडताळणी केली जाते.",
  "GET IN TOUCH":"संपर्क साधा","Contact Sahyadri Pool & Trip":"सह्याद्री पूल अँड ट्रिपशी संपर्क करा","Questions, booking support, partnerships or feedback — send us a message directly from the website.":"प्रश्न, बुकिंग मदत, भागीदारी किंवा अभिप्राय — वेबसाइटवरून थेट संदेश पाठवा.","SAHYADRI POOL & TRIP":"सह्याद्री पूल अँड ट्रिप","We’re here to help.":"आम्ही मदतीसाठी येथे आहोत.","For support, partnership enquiries and general questions, use the form. Your message is submitted to the platform support inbox.":"मदत, भागीदारी किंवा सामान्य प्रश्नांसाठी फॉर्म वापरा. तुमचा संदेश थेट सपोर्ट इनबॉक्समध्ये पाठवला जातो.","PHONE":"फोन","EMAIL":"ईमेल","OFFICE / BASE":"कार्यालय / बेस","SUPPORT HOURS":"सपोर्ट वेळ","SEND A MESSAGE":"संदेश पाठवा","How can we help?":"आम्ही कशी मदत करू शकतो?","We’ll receive your message directly. No email application is required.":"तुमचा संदेश आम्हाला थेट मिळेल. ईमेल अॅप उघडण्याची गरज नाही.","NAME *":"नाव *","SUBJECT *":"विषय *","MESSAGE *":"संदेश *","Send Message":"संदेश पाठवा","Your message is sent securely to the Sahyadri support inbox through the website backend.":"तुमचा संदेश वेबसाइटच्या बॅकएंडद्वारे सुरक्षितपणे सपोर्ट इनबॉक्समध्ये पाठवला जातो.",
  "STAY PARTNER ONBOARDING":"निवास भागीदार नोंदणी","Hotel & Homestay Registration":"हॉटेल आणि होमस्टे नोंदणी","Submit your property and owner details. Admin approval is required before a Stay Owner account can publish properties.":"तुमच्या मालमत्ता आणि मालकाची माहिती सादर करा. मालमत्ता प्रकाशित करण्यापूर्वी प्रशासकीय मंजुरी आवश्यक आहे.","OWNER DETAILS":"मालकाची माहिती","Your account":"तुमचे खाते","OWNER / CONTACT NAME *":"मालक / संपर्क नाव *","MOBILE *":"मोबाईल *","PASSWORD *":"पासवर्ड *","PROPERTY DETAILS":"मालमत्तेची माहिती","Tell us about your stay":"तुमच्या निवासाबद्दल सांगा","PROPERTY / HOTEL NAME *":"मालमत्ता / हॉटेलचे नाव *","PROPERTY TYPE *":"मालमत्तेचा प्रकार *","ADDRESS *":"पत्ता *","CITY / DISTRICT *":"शहर / जिल्हा *","TOTAL ROOMS *":"एकूण खोल्या *","STARTING PRICE / NIGHT (₹) *":"प्रति रात्र सुरुवातीची किंमत (₹) *","PROPERTY DESCRIPTION *":"मालमत्तेचे वर्णन *","I confirm that the information is accurate and I agree to admin verification before activation.":"मी दिलेली माहिती अचूक आहे आणि सक्रिय करण्यापूर्वी प्रशासकीय पडताळणीस सहमत आहे.","Submit Registration":"नोंदणी सादर करा"
});
Object.assign(SITE_TEXT_I18N.hi, {
  "PARTNER WITH SAHYADRI":"सह्याद्री के साथ पार्टनर बनें","Grow your travel business with Sahyadri":"सह्याद्री के साथ अपना यात्रा व्यवसाय बढ़ाएँ","Choose the partner path that matches your service. Verification keeps the platform reliable for travelers.":"अपनी सेवा के अनुसार पार्टनर विकल्प चुनें। सत्यापन यात्रियों के लिए प्लेटफॉर्म को भरोसेमंद रखता है.","TRANSPORT":"परिवहन","Driver Partner":"ड्राइवर पार्टनर","Publish shared trips, manage seats and serve travelers across Maharashtra.":"साझा यात्राएँ प्रकाशित करें, सीटें प्रबंधित करें और महाराष्ट्र में यात्रियों की सेवा करें.","Driver Registration":"ड्राइवर पंजीकरण","BUSINESS":"व्यवसाय","Travel Agency":"ट्रैवल एजेंसी","Submit your agency details and required documents for admin verification.":"अपनी एजेंसी की जानकारी और आवश्यक दस्तावेज़ प्रशासनिक सत्यापन के लिए जमा करें.","Agency Registration":"एजेंसी पंजीकरण","STAYS":"स्टे","Hotel / Stay Owner":"होटल / स्टे मालिक","Register your hotel or homestay for an admin-reviewed stay partner account.":"अपने होटल या होमस्टे को प्रशासनिक समीक्षा के लिए पंजीकृत करें.","Hotel Registration":"होटल पंजीकरण","Professional onboarding":"प्रोफेशनल ऑनबोर्डिंग","Partner applications are reviewed before partner-only features are enabled.":"पार्टनर सुविधाएँ शुरू करने से पहले आवेदनों की समीक्षा की जाती है.",
  "GET IN TOUCH":"संपर्क करें","Contact Sahyadri Pool & Trip":"सह्याद्री पूल एंड ट्रिप से संपर्क करें","Questions, booking support, partnerships or feedback — send us a message directly from the website.":"सवाल, बुकिंग सहायता, पार्टनरशिप या फीडबैक — वेबसाइट से सीधे संदेश भेजें.","SAHYADRI POOL & TRIP":"सह्याद्री पूल एंड ट्रिप","We’re here to help.":"हम आपकी मदद के लिए यहाँ हैं.","For support, partnership enquiries and general questions, use the form. Your message is submitted to the platform support inbox.":"सहायता, पार्टनरशिप या सामान्य सवालों के लिए फॉर्म का उपयोग करें. आपका संदेश सीधे सपोर्ट इनबॉक्स में भेजा जाता है.","PHONE":"फोन","EMAIL":"ईमेल","OFFICE / BASE":"कार्यालय / बेस","SUPPORT HOURS":"सपोर्ट समय","SEND A MESSAGE":"संदेश भेजें","How can we help?":"हम आपकी कैसे मदद कर सकते हैं?","We’ll receive your message directly. No email application is required.":"आपका संदेश हमें सीधे मिलेगा. ईमेल ऐप खोलने की जरूरत नहीं है.","NAME *":"नाम *","SUBJECT *":"विषय *","MESSAGE *":"संदेश *","Send Message":"संदेश भेजें","Your message is sent securely to the Sahyadri support inbox through the website backend.":"आपका संदेश वेबसाइट बैकएंड के माध्यम से सुरक्षित रूप से सपोर्ट इनबॉक्स में भेजा जाता है.",
  "STAY PARTNER ONBOARDING":"स्टे पार्टनर पंजीकरण","Hotel & Homestay Registration":"होटल और होमस्टे पंजीकरण","Submit your property and owner details. Admin approval is required before a Stay Owner account can publish properties.":"अपनी प्रॉपर्टी और मालिक की जानकारी जमा करें. प्रॉपर्टी प्रकाशित करने से पहले प्रशासनिक मंजूरी आवश्यक है.","OWNER DETAILS":"मालिक की जानकारी","Your account":"आपका खाता","OWNER / CONTACT NAME *":"मालिक / संपर्क नाम *","MOBILE *":"मोबाइल *","PASSWORD *":"पासवर्ड *","PROPERTY DETAILS":"प्रॉपर्टी की जानकारी","Tell us about your stay":"अपने स्टे के बारे में बताएं","PROPERTY / HOTEL NAME *":"प्रॉपर्टी / होटल का नाम *","PROPERTY TYPE *":"प्रॉपर्टी का प्रकार *","ADDRESS *":"पता *","CITY / DISTRICT *":"शहर / जिला *","TOTAL ROOMS *":"कुल कमरे *","STARTING PRICE / NIGHT (₹) *":"प्रति रात शुरुआती कीमत (₹) *","PROPERTY DESCRIPTION *":"प्रॉपर्टी विवरण *","I confirm that the information is accurate and I agree to admin verification before activation.":"मैं पुष्टि करता/करती हूँ कि जानकारी सही है और सक्रिय होने से पहले प्रशासनिक सत्यापन के लिए सहमत हूँ.","Submit Registration":"पंजीकरण जमा करें"
});
Object.assign(SITE_TEXT_I18N.mr,{
  "SAHYADRI ECOSYSTEM":"सह्याद्री इकोसिस्टम","Everything you need, without the clutter.":"गोंधळ न करता आवश्यक सगळ्या सुविधा एका ठिकाणी.","मुख्य सुविधा इथेच. बाकी tools योग्य ठिकाणी menu आणि संबंधित pages मधून मिळतील.":"मुख्य सुविधा इथेच. बाकी साधने मेनू आणि संबंधित पेजमधून मिळतील.","Explore":"एक्सप्लोर","Forts, Sahyadri destinations, routes and Bharat travel.":"किल्ले, सह्याद्री डेस्टिनेशन्स, ट्रेल्स आणि भारतातील प्रवास.","Explore Places":"ठिकाणे एक्सप्लोर करा","Travel":"प्रवास","Shared trips, pool rides and return journeys.":"शेअर्ड ट्रिप्स, पूल राईड्स आणि परतीचे प्रवास.","Find a Trip":"प्रवास शोधा","Stay":"निवास","Hotels, homestays and verified local stays.":"हॉटेल, होमस्टे आणि सत्यापित स्थानिक निवास.","Find a Stay":"निवास शोधा","Community":"कम्युनिटी","Trek mates, traveller updates, photos and badges.":"ट्रेक साथीदार, प्रवासी अपडेट्स, फोटो आणि बॅजेस.","Join Community":"कम्युनिटीमध्ये जा","Safety":"सुरक्षितता","SOS, emergency help, GPX trails and safety tools.":"SOS, आपत्कालीन मदत, GPX ट्रेल्स आणि सुरक्षा साधने.","Open Safety":"सुरक्षितता उघडा","Smart":"स्मार्ट","AI trek suggestions and live weather insights.":"AI ट्रेक सूचना आणि लाईव्ह हवामान माहिती.","Explore Smart":"स्मार्ट सुविधा पाहा","NEW":"नवीन","Plan smarter. Trek safer. Travel together.":"स्मार्ट प्लॅन करा. सुरक्षित ट्रेक करा. सोबत प्रवास करा.","Explore Community":"कम्युनिटी एक्सप्लोर करा","Offline Trails":"ऑफलाइन ट्रेल्स","Sahyadri Community":"सह्याद्री कम्युनिटी","Connect with fellow trekkers, share current trail information and build a safer trekking community.":"इतर ट्रेकर्सशी जोडा, ताज्या ट्रेल माहिती शेअर करा आणि सुरक्षित ट्रेकिंग कम्युनिटी तयार करा.","Trek Mate Finder":"ट्रेक साथीदार शोधा","Plan a trek with people who are interested in the same destination, date and difficulty.":"त्याच ठिकाण, तारीख आणि अवघडीनुसार ट्रेक साथीदार शोधा.","Leaderboard & Badges":"लीडरबोर्ड आणि बॅजेस","Offline Trails & GPX":"ऑफलाइन ट्रेल्स आणि GPX","Live Weather":"लाईव्ह हवामान","AI Trek Recommender":"AI ट्रेक शिफारस"
});
Object.assign(SITE_TEXT_I18N.hi,{
  "SAHYADRI ECOSYSTEM":"सह्याद्री इकोसिस्टम","Everything you need, without the clutter.":"बिना किसी उलझन के आपकी जरूरत की सभी सुविधाएँ।","मुख्य सुविधा इथेच. बाकी tools योग्य ठिकाणी menu आणि संबंधित pages मधून मिळतील.":"मुख्य सुविधाएँ यहाँ हैं। बाकी टूल्स मेनू और संबंधित पेजों में मिलेंगे।","Explore":"एक्सप्लोर","Forts, Sahyadri destinations, routes and Bharat travel.":"किले, सह्याद्री डेस्टिनेशन, ट्रेल्स और भारत यात्रा।","Explore Places":"जगहें देखें","Travel":"यात्रा","Shared trips, pool rides and return journeys.":"शेयर की गई यात्राएँ, पूल राइड्स और वापसी यात्राएँ।","Find a Trip":"यात्रा खोजें","Stay":"ठहरना","Hotels, homestays and verified local stays.":"होटल, होमस्टे और सत्यापित स्थानीय ठहरने की जगहें।","Find a Stay":"स्टे खोजें","Community":"कम्युनिटी","Trek mates, traveller updates, photos and badges.":"ट्रेक साथी, यात्री अपडेट, फोटो और बैज।","Join Community":"कम्युनिटी में जाएँ","Safety":"सुरक्षा","SOS, emergency help, GPX trails and safety tools.":"SOS, आपातकालीन सहायता, GPX ट्रेल्स और सुरक्षा टूल्स।","Open Safety":"सुरक्षा खोलें","Smart":"स्मार्ट","AI trek suggestions and live weather insights.":"AI ट्रेक सुझाव और लाइव मौसम जानकारी।","Explore Smart":"स्मार्ट सुविधा देखें","NEW":"नया","Plan smarter. Trek safer. Travel together.":"स्मार्ट प्लान करें। सुरक्षित ट्रेक करें। साथ यात्रा करें।","Explore Community":"कम्युनिटी देखें","Offline Trails":"ऑफलाइन ट्रेल्स","Sahyadri Community":"सह्याद्री कम्युनिटी","Connect with fellow trekkers, share current trail information and build a safer trekking community.":"अन्य ट्रेकर्स से जुड़ें, ताजा ट्रेल जानकारी साझा करें और सुरक्षित ट्रेकिंग कम्युनिटी बनाएं।","Trek Mate Finder":"ट्रेक साथी खोजें","Plan a trek with people who are interested in the same destination, date and difficulty.":"उसी जगह, तारीख और कठिनाई के अनुसार ट्रेक साथी खोजें।","Leaderboard & Badges":"लीडरबोर्ड और बैज","Offline Trails & GPX":"ऑफलाइन ट्रेल्स और GPX","Live Weather":"लाइव मौसम","AI Trek Recommender":"AI ट्रेक सुझाव"
});
function getLanguage(){return localStorage.getItem("spt_language")||"en";}
function toggleLanguageMenu(){document.getElementById("language-menu")?.classList.toggle("open");}
function setLanguage(lang){if(!SPT_TRANSLATIONS[lang])return;localStorage.setItem("spt_language",lang);document.getElementById("language-menu")?.classList.remove("open");render();}

const SPT_TEXT_ORIGINALS = new WeakMap();
function applyGenericLanguage(){
  const lang=getLanguage();
  const map=SITE_TEXT_I18N[lang]||SITE_TEXT_I18N.en;
  const walker=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT);
  const nodes=[]; let n; while((n=walker.nextNode())) nodes.push(n);
  nodes.forEach(node=>{
    if(!node.parentElement || node.parentElement.closest('script,style')) return;
    if(!SPT_TEXT_ORIGINALS.has(node)) SPT_TEXT_ORIGINALS.set(node,node.nodeValue);
    const original=SPT_TEXT_ORIGINALS.get(node);
    const trimmed=original.trim();
    if(map[trimmed]){
      const start=original.indexOf(trimmed), end=start+trimmed.length;
      node.nodeValue=original.slice(0,start)+map[trimmed]+original.slice(end);
    } else node.nodeValue=original;
  });
  document.querySelectorAll("input[placeholder],textarea[placeholder]").forEach(el=>{
    if(!el.dataset.sptOriginalPlaceholder) el.dataset.sptOriginalPlaceholder=el.getAttribute('placeholder')||'';
    const key=el.dataset.sptOriginalPlaceholder;
    el.setAttribute('placeholder',map[key]||key);
  });
}
function applyLanguage(){
  const lang=getLanguage(),dict=SPT_TRANSLATIONS[lang]||SPT_TRANSLATIONS.en;
  document.querySelectorAll('[data-i18n]').forEach(el=>{const k=el.getAttribute('data-i18n');if(dict[k])el.textContent=dict[k];});
  const c=document.getElementById('current-language');
  if(c)c.textContent=lang==='mr'?'मराठी':lang==='hi'?'हिन्दी':'English';
  document.documentElement.lang=lang;
  applyGenericLanguage();
}
document.addEventListener("click",e=>{if(!e.target.closest(".language-switcher"))document.getElementById("language-menu")?.classList.remove("open");});
function toggleMobile() { toggleSettingsPanel(); }

function closeSettingsPanel() {
  document.getElementById("settings-overlay")?.classList.remove("open");
  document.getElementById("settings-panel")?.classList.remove("open");
  document.body.classList.remove("drawer-open");
}

function toggleSettingsPanel() {
  const overlay = document.getElementById("settings-overlay");
  const panel = document.getElementById("settings-panel");
  if (!overlay || !panel) return;
  const willOpen = !panel.classList.contains("open");
  overlay.classList.toggle("open", willOpen);
  panel.classList.toggle("open", willOpen);
  document.body.classList.toggle("drawer-open", willOpen);
}

function openGlobalSearch() {
  if (document.getElementById("global-search-modal")) return;
  document.body.insertAdjacentHTML("beforeend", `<div class="global-search-backdrop" id="global-search-modal" onclick="closeGlobalSearch(event)">
    <div class="global-search-panel" onclick="event.stopPropagation()">
      <div class="global-search-head"><div><span class="settings-kicker">QUICK SEARCH</span><h2>Search Sahyadri</h2></div><button class="close" onclick="closeGlobalSearch()">×</button></div>
      <div class="global-search-input"><span>⌕</span><input id="global-search-input" autofocus oninput="runGlobalSearch()" placeholder="Search forts, districts or trips..."><span class="search-key-hint">↵</span></div>
      <div id="global-search-results" class="global-search-results">${loading("Type to search…")}</div>
    </div>
  </div>`);
  document.getElementById("global-search-input")?.addEventListener("keydown", e => { if (e.key === "Enter") globalSearchEnter(); });
  Promise.all([ensureFortsLoaded(), loadSpotsForSearch()]).then(runGlobalSearch).catch(() => runGlobalSearch());
}

function closeGlobalSearch(e) {
  if (e && e.target?.id !== "global-search-modal") return;
  document.getElementById("global-search-modal")?.remove();
}

async function loadSpotsForSearch(){
  if (SPOTS.length) return SPOTS;
  try { const data=await API.get("/api/spots"); SPOTS=Array.isArray(data)?data:[]; window.SPOTS=SPOTS; return SPOTS; }
  catch(e){ console.warn("Spot search data unavailable:",e); return []; }
}

function runGlobalSearch() {
  const q = document.getElementById("global-search-input")?.value.trim().toLowerCase() || "";
  const el = document.getElementById("global-search-results");
  if (!el) return;
  if (!q) { el.innerHTML = `<div class="search-empty">Search forts, districts, stays and trips.</div>`; return; }
  const fortMatches = FORTS.filter(f => [f.fortName, f.district, f.locality, f.baseVillage].some(v => String(v || "").toLowerCase().includes(q))).slice(0, 6);
  const spotMatches = (window.SPOTS || []).filter(s => [s.name, s.category, s.district, s.locality].some(v => String(v || "").toLowerCase().includes(q))).slice(0, 6);
  const tripMatches = TRIPS.filter(t => [t.source, t.destination].some(v => String(v || "").toLowerCase().includes(q))).slice(0, 4);
  const results = [
    ...fortMatches.map(f => `<button class="global-result" onclick="closeGlobalSearch();openFortFromSearch('${esc(f.fortId)}')"><span class="result-icon">🏰</span><span><b>${esc(f.fortName)}</b><small>Fort · ${esc(f.district || "Maharashtra")}</small></span></button>`),
    ...spotMatches.map(sp => `<button class="global-result" onclick="closeGlobalSearch();openSpotFromSearch('${encodeURIComponent(sp.spotId)}')"><span class="result-icon">🌿</span><span><b>${esc(sp.name)}</b><small>${esc(sp.category || "Spot")} · ${esc(sp.district || "Maharashtra")}</small></span></button>`),
    ...tripMatches.map(t => `<button class="global-result" onclick="closeGlobalSearch();state.search.destination='${esc(t.destination)}';go('trips')"><span class="result-icon">↗</span><span><b>${esc(t.source)} → ${esc(t.destination)}</b><small>Trip</small></span><span>→</span></button>`)
  ];
  el.innerHTML = results.length ? results.join("") : `<div class="search-empty">No matching forts, spots or trips found.</div>`;
}

function globalSearchEnter() {
  const first = document.querySelector(".global-result");
  if (first) first.click();
  else {
    const q = document.getElementById("global-search-input")?.value.trim();
    if (q) { closeGlobalSearch(); fortSearchTerm = q; go("forts"); }
  }
}

function openFortFromSearch(id) {
  if (state.page !== "forts") {
    go("forts");
    setTimeout(() => openFortDetails(id), 100);
  } else openFortDetails(id);
}

function settingsPanel() {
  const theme = document.documentElement.dataset.theme === "dark" ? "dark" : "light";
  return `<div id="settings-overlay" class="settings-overlay" onclick="closeSettingsPanel()"></div>
  <aside id="settings-panel" class="settings-panel" aria-label="Menu and settings">
    <div class="settings-head">
      <div>
        <span class="settings-kicker">SAHYADRI POOL & TRIP</span>
        <h2>Menu & Settings</h2>
      </div>
      <button class="settings-close" type="button" onclick="closeSettingsPanel()" aria-label="Close menu">×</button>
    </div>

    <div class="settings-section">
      <span class="settings-label">QUICK NAVIGATION</span>
      <a href="#home" onclick="closeSettingsPanel()" data-i18n="menu.home">Home</a>
      <a href="#trips" onclick="closeSettingsPanel()" data-i18n="menu.trips">Find Trips</a>
      <a href="#forts" onclick="closeSettingsPanel()" data-i18n="menu.forts">Maharashtra Forts</a>
      <a href="#spots" onclick="closeSettingsPanel()" data-i18n="menu.spots">Sahyadri Destinations</a>
      <a href="#india-nature" onclick="closeSettingsPanel()">Bharat Destinations</a>
      <a href="#stays" onclick="closeSettingsPanel()" data-i18n="menu.stays">Stays & Properties</a>
      <a href="#return" onclick="closeSettingsPanel()" data-i18n="menu.return">Return Trips</a>
      <a href="#about" onclick="closeSettingsPanel()" data-i18n="menu.about">About Us</a>
      <a href="#how" onclick="closeSettingsPanel()">How It Works</a>
      <a href="#contact" onclick="closeSettingsPanel()" data-i18n="menu.contact">Contact Us</a>
    </div>

    <div class="settings-section">
      <span class="settings-label">COMMUNITY</span>
      <a href="#community" onclick="closeSettingsPanel()">Community Hub</a>
      <a href="#trek-mates" onclick="closeSettingsPanel()">Trek Mate Finder</a>
      <a href="#leaderboard" onclick="closeSettingsPanel()">Leaderboard & Badges</a>
    </div>

    <div class="settings-section">
      <span class="settings-label">LOCAL & EXPERIENCES</span>
      <a href="#guides" onclick="closeSettingsPanel()">Local Guides & Experiences</a>
      <a href="#stays" onclick="closeSettingsPanel()">Base Village Stays</a>
    </div>

    <div class="settings-section">
      <span class="settings-label">SMART TRAVEL</span>
      <a href="#weather" onclick="closeSettingsPanel()">Live Weather</a>
      <a href="#ai-recommendations" onclick="closeSettingsPanel()">AI Trek Recommender</a>
    </div>

    <div class="settings-section">
      <span class="settings-label">OFFLINE & SAFETY TOOLS</span>
      <a href="#offline-trails" onclick="closeSettingsPanel()">Offline Trails & GPX</a>
      <a href="#safety" onclick="closeSettingsPanel()" data-i18n="menu.safety">Safety & SOS</a>
      <a href="#complaint" onclick="closeSettingsPanel()" data-i18n="menu.complaint">Help & Complaint</a>
    </div>

    <div class="settings-section">
      <span class="settings-label">ACCOUNT & SAFETY</span>
      <a href="#profile" onclick="closeSettingsPanel()" data-i18n="menu.profile">Profile</a>
      ${state.user ? '<a href="#settings-password" onclick="closeSettingsPanel()">Change Password</a>' : ''}
      <a href="#partner" onclick="closeSettingsPanel()" data-i18n="menu.partner">Partner With Us</a>
    </div>

    <div class="settings-section">
      <span class="settings-label">LEGAL & INFORMATION</span>
      <a href="#privacy" onclick="closeSettingsPanel()" data-i18n="menu.privacy">Privacy Policy</a>
      <a href="#terms" onclick="closeSettingsPanel()" data-i18n="menu.terms">Terms & Conditions</a>
      <a href="#refund" onclick="closeSettingsPanel()" data-i18n="menu.refund">Cancellation & Refund</a>
      <a href="#trust" onclick="closeSettingsPanel()" data-i18n="menu.trust">Trust & Partners</a>
    </div>

    <div class="settings-section settings-theme">
      <span class="settings-label">APPEARANCE</span>
      <button type="button" class="theme-choice ${theme === "light" ? "active" : ""}" onclick="setTheme('light')">
        <span>Light Mode</span><strong>LIGHT</strong>
      </button>
      <button type="button" class="theme-choice ${theme === "dark" ? "active" : ""}" onclick="setTheme('dark')">
        <span>Dark Mode</span><strong>DARK</strong>
      </button>
    </div>

    <div class="settings-contact">
      <span class="settings-label">FOUNDER & OWNER</span>
      <strong>Vishal Machhindra Shingte</strong>
      <a href="tel:+917620815801">7620815801</a>
      <a href="mailto:vishalshingte761@gmail.com">vishalshingte761@gmail.com</a>
      <small>A/P Ranjani, Ambegaon 410504<br>Pune - Maharashtra</small>
    </div>
  </aside>`;
}

function setTheme(theme) {
  localStorage.setItem("spt_theme", theme);
  document.documentElement.dataset.theme = theme;
  applyTheme();
  const panel = document.getElementById("settings-panel");
  if (panel) {
    panel.outerHTML = settingsPanel().match(/<aside[\s\S]*<\/aside>/)?.[0] || panel.outerHTML;
    const overlay = document.getElementById("settings-overlay");
    if (overlay) overlay.classList.add("open");
    const refreshed = document.getElementById("settings-panel");
    if (refreshed) refreshed.classList.add("open");
  }
}

// =====================================================
// FORTS — live from Spring Boot /api/forts
// =====================================================
let FORTS = [];
let fortSearchTerm = "";

const FORT_I18N = {
  en: {
    eyebrow:"MAHARASHTRA HERITAGE", title:"Explore Maharashtra Forts", subtitle:"Discover forts from the live Sahyadri registry. Open a fort for verified location, route, source and safety information.", search:"Search by fort, district, locality or base village…", loading:"Loading Maharashtra forts…", verified:"Verified record", registry:"Registry record", gps:"GPS available", gpsPending:"GPS pending", elevation:"Elevation pending", basePending:"Base village pending", view:"View Fort Details", readMore:"Read More", close:"Close", overview:"Overview", location:"Location", routes:"Routes & Trek Information", verification:"Sources & Verification", district:"District", locality:"Locality", baseVillage:"Base village", coordinates:"Coordinates", elevationLabel:"Elevation", bestSeason:"Best season", fortType:"Fort type", protection:"Protection status", parking:"Parking", water:"Water availability", network:"Network availability", monsoon:"Monsoon risk", recordStatus:"Record status", verificationStatus:"Verification status", source:"Primary source", locationSource:"Location source", locationConfidence:"Location confidence", verifiedDate:"Location verified date", lastVerified:"Last verified", datasetScope:"Dataset scope", route:"Route", routeStart:"Start", routeEnd:"End", distance:"Distance", duration:"Duration", difficulty:"Difficulty", routeType:"Type", routeSource:"Route source", routeVerification:"Route verification", gpsStatus:"GPS status", trekStatus:"Trek status", routeConfidence:"Route confidence", trekStart:"Trek start", trekRoute:"Trek route", trekDistance:"Trek distance", trekDuration:"Trek duration", noRoute:"No route is currently available in the live route registry for this fort.", routeUnavailable:"Route details are currently unavailable.", directions:"Get Directions", map:"Open Fort Location", useLocation:"Use My Location", locationDenied:"Location permission was not available. Opening the fort location instead.", locationError:"Could not access your location. Opening the fort location instead.", searching:"Opening Google Maps…", searchEmpty:"No forts match your search.", gpsUnavailable:"Fort GPS is not available yet.", count:"forts"},
  mr: {
    eyebrow:"महाराष्ट्र वारसा", title:"महाराष्ट्रातील किल्ले शोधा", subtitle:"Sahyadri नोंदणीतील किल्ले शोधा. किल्ल्यावर क्लिक करून स्थान, मार्ग, स्रोत आणि सुरक्षिततेची उपलब्ध माहिती पाहा.", search:"किल्ला, जिल्हा, परिसर किंवा बेस गाव शोधा…", loading:"महाराष्ट्रातील किल्ले लोड होत आहेत…", verified:"सत्यापित नोंद", registry:"नोंदणीतील नोंद", gps:"GPS उपलब्ध", gpsPending:"GPS प्रलंबित", elevation:"उंची प्रलंबित", basePending:"बेस गाव प्रलंबित", view:"किल्ल्याची माहिती", readMore:"अधिक वाचा", close:"बंद करा", overview:"आढावा", location:"स्थान", routes:"मार्ग आणि ट्रेक माहिती", verification:"स्रोत आणि पडताळणी", district:"जिल्हा", locality:"परिसर", baseVillage:"बेस गाव", coordinates:"निर्देशांक", elevationLabel:"उंची", bestSeason:"योग्य हंगाम", fortType:"किल्ल्याचा प्रकार", protection:"संरक्षण स्थिती", parking:"पार्किंग", water:"पाण्याची उपलब्धता", network:"नेटवर्क उपलब्धता", monsoon:"पावसाळी जोखीम", recordStatus:"नोंद स्थिती", verificationStatus:"पडताळणी स्थिती", source:"मुख्य स्रोत", locationSource:"स्थान स्रोत", locationConfidence:"स्थान विश्वसनीयता", verifiedDate:"स्थान पडताळणी तारीख", lastVerified:"शेवटची पडताळणी", datasetScope:"डेटासेट व्याप्ती", route:"मार्ग", routeStart:"सुरुवात", routeEnd:"शेवट", distance:"अंतर", duration:"कालावधी", difficulty:"अवघडपणा", routeType:"प्रकार", routeSource:"मार्ग स्रोत", routeVerification:"मार्ग पडताळणी", gpsStatus:"GPS स्थिती", trekStatus:"ट्रेक स्थिती", routeConfidence:"मार्ग विश्वसनीयता", trekStart:"ट्रेक सुरुवात", trekRoute:"ट्रेक मार्ग", trekDistance:"ट्रेक अंतर", trekDuration:"ट्रेक कालावधी", noRoute:"या किल्ल्यासाठी सध्या थेट मार्ग नोंदणी उपलब्ध नाही.", routeUnavailable:"मार्गाची माहिती सध्या उपलब्ध नाही.", directions:"दिशानिर्देश", map:"किल्ल्याचे स्थान उघडा", useLocation:"माझे स्थान वापरा", locationDenied:"स्थान परवानगी उपलब्ध नाही. किल्ल्याचे स्थान उघडत आहे.", locationError:"तुमचे स्थान मिळू शकले नाही. किल्ल्याचे स्थान उघडत आहे.", searching:"Google Maps उघडत आहे…", searchEmpty:"तुमच्या शोधाशी जुळणारे किल्ले सापडले नाहीत.", count:"किल्ले"},
  hi: {
    eyebrow:"महाराष्ट्र विरासत", title:"महाराष्ट्र के किले देखें", subtitle:"Sahyadri के लाइव किला रजिस्टर से किले खोजें। स्थान, मार्ग, स्रोत और सुरक्षा की उपलब्ध जानकारी देखने के लिए किला खोलें।", search:"किला, जिला, क्षेत्र या बेस गांव खोजें…", loading:"महाराष्ट्र के किले लोड हो रहे हैं…", verified:"सत्यापित रिकॉर्ड", registry:"रजिस्टर रिकॉर्ड", gps:"GPS उपलब्ध", gpsPending:"GPS लंबित", elevation:"ऊंचाई लंबित", basePending:"बेस गांव लंबित", view:"किले की जानकारी", readMore:"और पढ़ें", close:"बंद करें", overview:"अवलोकन", location:"स्थान", routes:"मार्ग और ट्रेक जानकारी", verification:"स्रोत और सत्यापन", district:"जिला", locality:"क्षेत्र", baseVillage:"बेस गांव", coordinates:"निर्देशांक", elevationLabel:"ऊंचाई", bestSeason:"उपयुक्त मौसम", fortType:"किले का प्रकार", protection:"संरक्षण स्थिति", parking:"पार्किंग", water:"पानी की उपलब्धता", network:"नेटवर्क उपलब्धता", monsoon:"मानसून जोखिम", recordStatus:"रिकॉर्ड स्थिति", verificationStatus:"सत्यापन स्थिति", source:"मुख्य स्रोत", locationSource:"स्थान स्रोत", locationConfidence:"स्थान विश्वसनीयता", verifiedDate:"स्थान सत्यापन तारीख", lastVerified:"अंतिम सत्यापन", datasetScope:"डेटासेट दायरा", route:"मार्ग", routeStart:"शुरुआत", routeEnd:"अंत", distance:"दूरी", duration:"अवधि", difficulty:"कठिनाई", routeType:"प्रकार", routeSource:"मार्ग स्रोत", routeVerification:"मार्ग सत्यापन", gpsStatus:"GPS स्थिति", trekStatus:"ट्रेक स्थिति", routeConfidence:"मार्ग विश्वसनीयता", trekStart:"ट्रेक शुरुआत", trekRoute:"ट्रेक मार्ग", trekDistance:"ट्रेक दूरी", trekDuration:"ट्रेक अवधि", noRoute:"इस किले के लिए लाइव रूट रजिस्टर में अभी कोई मार्ग उपलब्ध नहीं है।", routeUnavailable:"मार्ग की जानकारी अभी उपलब्ध नहीं है।", directions:"दिशा-निर्देश", map:"किले का स्थान खोलें", useLocation:"मेरा स्थान उपयोग करें", locationDenied:"स्थान अनुमति उपलब्ध नहीं है। किले का स्थान खोला जा रहा है।", locationError:"आपका स्थान नहीं मिल सका। किले का स्थान खोला जा रहा है।", searching:"Google Maps खुल रहा है…", searchEmpty:"आपकी खोज से मेल खाते किले नहीं मिले।", count:"किले"}
};
function fortT(key){const lang=getLanguage();return FORT_I18N[lang]?.[key] ?? FORT_I18N.en[key] ?? key;}

function forts() {
  return `${header()}<main class="page forts-page"><div class="page-header fort-hero-header"><div class="container">
    <span class="eyebrow page-eyebrow">${fortT("eyebrow")}</span>
    <h1>${fortT("title")}</h1>
    <p class="section-sub">${fortT("subtitle")}</p>
  </div></div>
  <section class="section"><div class="container">
    <div class="fort-toolbar">
      <div class="fort-search-box">
        <span class="fort-search-icon" aria-hidden="true">⌕</span>
        <input id="fort-search" value="${esc(fortSearchTerm)}" oninput="filterFortCards()" placeholder="${fortT("search")}" aria-label="${fortT("search")}">
        <button type="button" class="fort-clear" onclick="clearFortSearch()" aria-label="${fortT("close")}">×</button>
      </div>
      <div class="fort-count" id="fort-count">…</div>
    </div>
    <div id="fort-list" class="fort-grid">${loading(fortT("loading"))}</div>
  </div></section></main>${footer()}`;
}

async function ensureFortsLoaded() {
  if (FORTS.length) return FORTS;
  const data = await API.get("/api/forts");
  FORTS = Array.isArray(data) ? data : [];
  return FORTS;
}
async function loadForts() {
  const container = document.getElementById("fort-list");
  if (!container) return;
  try { await ensureFortsLoaded(); renderFortCards(); }
  catch (error) { console.error("Failed to load forts:", error); container.innerHTML = errorBlock(error.message || "Unable to load forts."); setText("fort-count", "—"); }
}
function renderFortCards() {
  const container = document.getElementById("fort-list"); if (!container) return;
  const q = fortSearchTerm.trim().toLowerCase();
  const filtered = FORTS.filter(f => [f.fortName, f.district, f.locality, f.baseVillage].some(v => String(v || "").toLowerCase().includes(q)));
  setText("fort-count", `${filtered.length} / ${FORTS.length} ${fortT("count")}`);
  container.innerHTML = filtered.length ? filtered.map(fortCard).join("") : emptyBlock(fortT("searchEmpty")); if(filtered.length) hydrateFortPhotos(filtered);
}
function filterFortCards(){fortSearchTerm=document.getElementById("fort-search")?.value||"";renderFortCards();}
function clearFortSearch(){fortSearchTerm="";const i=document.getElementById("fort-search");if(i)i.value="";renderFortCards();}

function isReal(v){return v!==null && v!==undefined && String(v).trim()!=="";}
function formatValue(v, fallback="PENDING"){return isReal(v)?esc(v):fallback;}
function fortVerified(f){return String(f.verificationStatus||"").toLowerCase().includes("verified") || String(f.locationConfidence||"").toLowerCase().includes("complete");}
function fortCard(f){
  const hasGps=isReal(f.latitude)&&isReal(f.longitude);
  const verified=fortVerified(f);
  const summary=[f.locality,f.baseVillage].filter(isReal).map(esc).join(" · ");
  const highlights=[];
  if(isReal(f.district)) highlights.push(`<span><small>${fortT("district")}</small><b>${esc(f.district)}</b></span>`);
  if(isReal(f.baseVillage)) highlights.push(`<span><small>${fortT("baseVillage")}</small><b>${esc(f.baseVillage)}</b></span>`);
  if(isReal(f.elevationM)) highlights.push(`<span><small>${fortT("elevationLabel")}</small><b>${esc(f.elevationM)} m</b></span>`);
  if(isReal(f.bestSeason)) highlights.push(`<span><small>${fortT("bestSeason")}</small><b>${esc(f.bestSeason)}</b></span>`);
  return `<article class="fort-card">
    <div class="fort-photo-wrap"><div class="fort-photo-placeholder" data-fort-id="${esc(f.fortId||"")}" data-fort-photo="${esc(f.fortName||"")}"><span>🏰</span><small>Loading fort photo…</small></div></div>
    <div class="fort-card-top"><span class="tag">${verified?fortT("verified"):fortT("registry")}</span><span class="fort-id">${esc(f.fortId||"")}</span></div>
    <h3>${esc(f.fortName||"Unnamed Fort")}</h3>
    <p class="fort-location">${summary||esc(f.district||"Maharashtra")}</p>
    <div class="fort-card-highlights">${highlights.slice(0,4).join("")}</div>
    <div class="fort-card-bottom">
      ${hasGps?`<button type="button" class="fort-gps-chip" onclick="navigateToFort('${esc(f.fortId)}')"><span>⌖</span>${fortT("directions")}</button>`:`<span class="fort-pending">${fortT("gpsPending")}</span>`}
      <button class="btn btn-primary btn-sm fort-view-btn" onclick="openFortDetails('${esc(f.fortId)}')">${fortT("view")}</button>
    </div>
  </article>`;
}

let PHOTO_REGISTRY=null;
const SPECIAL_DESTINATION_PHOTOS = {
  "rajgad fort": {
    url: "https://commons.wikimedia.org/wiki/Special:FilePath/Suvela_Machi_27_Oct%2724.jpg?width=1400",
    credit: "Wikimedia Commons · Suvela Machi",
    source: "https://commons.wikimedia.org/wiki/File:Suvela_Machi_27_Oct%2724.jpg"
  },
  "rajgad": {
    url: "https://commons.wikimedia.org/wiki/Special:FilePath/Suvela_Machi_27_Oct%2724.jpg?width=1400",
    credit: "Wikimedia Commons · Suvela Machi",
    source: "https://commons.wikimedia.org/wiki/File:Suvela_Machi_27_Oct%2724.jpg"
  },
  "kalsubai": {
    url: "https://commons.wikimedia.org/wiki/Special:FilePath/Kalsubai_Temple_FrontView.jpg?width=1400",
    secondaryUrl: "https://commons.wikimedia.org/wiki/Special:FilePath/Sunrise_from_Kalsubai_peak_1.jpg?width=1100",
    credit: "Wikimedia Commons · Kalsubai Temple + sunrise",
    source: "https://commons.wikimedia.org/wiki/Category:Kalsubai_Mata_Temple"
  },
  "kalsubai peak": {
    url: "https://commons.wikimedia.org/wiki/Special:FilePath/Kalsubai_Temple_FrontView.jpg?width=1400",
    secondaryUrl: "https://commons.wikimedia.org/wiki/Special:FilePath/Sunrise_from_Kalsubai_peak_1.jpg?width=1100",
    credit: "Wikimedia Commons · Kalsubai Temple + sunrise",
    source: "https://commons.wikimedia.org/wiki/Category:Kalsubai_Mata_Temple"
  },
  "moreshwar ganpati": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Entrance_gateway_of_Shri_Mayureshwar_Mandir%2C_Morgaon.jpg?width=1200", credit: "Wikimedia Commons · Mayureshwar, Morgaon", source: "https://commons.wikimedia.org/wiki/File:Entrance_gateway_of_Shri_Mayureshwar_Mandir,_Morgaon.jpg" },
  "siddhivinayak siddhatek": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Siddhi_Vinayak_at_Siddhatek.jpg?width=1200", credit: "Wikimedia Commons · Siddhatek", source: "https://commons.wikimedia.org/wiki/File:Siddhi_Vinayak_at_Siddhatek.jpg" },
  "siddhivinayak, siddhatek": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Siddhi_Vinayak_at_Siddhatek.jpg?width=1200", credit: "Wikimedia Commons · Siddhatek", source: "https://commons.wikimedia.org/wiki/File:Siddhi_Vinayak_at_Siddhatek.jpg" },
  "ballaleshwar pali": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Ballaleshwar_Temple_Pali_Raigad.jpg?width=1200", credit: "Wikimedia Commons · Ballaleshwar Pali", source: "https://commons.wikimedia.org/wiki/File:Ballaleshwar_Temple_Pali_Raigad.jpg" },
  "ballaleshwar, pali": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Ballaleshwar_Temple_Pali_Raigad.jpg?width=1200", credit: "Wikimedia Commons · Ballaleshwar Pali", source: "https://commons.wikimedia.org/wiki/File:Ballaleshwar_Temple_Pali_Raigad.jpg" },
  "varadavinayak mahad": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Varad_Vinayak_Entrance.jpg?width=1200", credit: "Wikimedia Commons · Varad Vinayak Mahad", source: "https://commons.wikimedia.org/wiki/File:Varad_Vinayak_Entrance.jpg" },
  "varadavinayak, mahad": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Varad_Vinayak_Entrance.jpg?width=1200", credit: "Wikimedia Commons · Varad Vinayak Mahad", source: "https://commons.wikimedia.org/wiki/File:Varad_Vinayak_Entrance.jpg" },
  "chintamani theur": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Theur_Chintamani_Temple.jpg?width=1200", credit: "Wikimedia Commons · Chintamani Theur", source: "https://commons.wikimedia.org/wiki/File:Theur_Chintamani_Temple.jpg" },
  "chintamani, theur": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Theur_Chintamani_Temple.jpg?width=1200", credit: "Wikimedia Commons · Chintamani Theur", source: "https://commons.wikimedia.org/wiki/File:Theur_Chintamani_Temple.jpg" },
  "girijatmaj lenyadri": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Lenyadri_Temple.jpg?width=1200", credit: "Wikimedia Commons · Lenyadri", source: "https://commons.wikimedia.org/wiki/Category:Lenyadri" },
  "girijatmaj, lenyadri": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Lenyadri_Temple.jpg?width=1200", credit: "Wikimedia Commons · Lenyadri", source: "https://commons.wikimedia.org/wiki/Category:Lenyadri" },
  "vighnahar ozar": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Ozhar_-_Shri_Vighnahar.jpg?width=1200", credit: "Wikimedia Commons · Vighnahar Ozar", source: "https://commons.wikimedia.org/wiki/File:Ozhar_-_Shri_Vighnahar.jpg" },
  "vighnahar, ozar": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/Ozhar_-_Shri_Vighnahar.jpg?width=1200", credit: "Wikimedia Commons · Vighnahar Ozar", source: "https://commons.wikimedia.org/wiki/File:Ozhar_-_Shri_Vighnahar.jpg" },
  "mahaganapati ranjangaon": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/MahaGanapati_RanjhanGaon.jpg?width=1200", credit: "Wikimedia Commons · Mahaganapati Ranjangaon", source: "https://commons.wikimedia.org/wiki/File:MahaGanapati_RanjhanGaon.jpg" },
  "mahaganapati, ranjangaon": { url: "https://commons.wikimedia.org/wiki/Special:FilePath/MahaGanapati_RanjhanGaon.jpg?width=1200", credit: "Wikimedia Commons · Mahaganapati Ranjangaon", source: "https://commons.wikimedia.org/wiki/File:MahaGanapati_RanjhanGaon.jpg" },
  "kalu waterfall": { url: "https://im.whatshot.in/img/2022/Aug/kullu-waterfall-save-tours-and-travels-cropped-1659349193.jpg", credit: "Online photo · WhatsHot / Save Tour And Travels", source: "https://www.whatshot.in/pune/the-highest-kalu-waterfall-in-malshej-ghat-c-37009" },
  "darya ghat": { url: "https://static.where-e.com/India/Maharashtra/Koukan_Division/Darya-Ghat_ea54bf5f97e9d195dc45bd491755191a.jpg", credit: "Online photo · Wheree", source: "https://darya-ghat.wheree.com/" }
};
function specialDestinationPhoto(name){ return SPECIAL_DESTINATION_PHOTOS[photoCoreName(name)] || null; }

let PHOTO_REGISTRY_PROMISE=null;
async function loadPhotoRegistry(){
  if(PHOTO_REGISTRY) return PHOTO_REGISTRY;
  if(!PHOTO_REGISTRY_PROMISE){PHOTO_REGISTRY_PROMISE=fetch('/data/MAHARASHTRA_PHOTO_REGISTRY.json',{cache:'force-cache'}).then(r=>r.ok?r.json():null).catch(()=>null);}
  PHOTO_REGISTRY=await PHOTO_REGISTRY_PROMISE;
  return PHOTO_REGISTRY;
}
function photoAsset(type,id){return PHOTO_REGISTRY?.assets?.find(x=>x.assetType===type&&String(x.assetId)===String(id));}
function photoCoreName(name){return String(name||'').toLowerCase().replace(/\bfort\b|\bfortress\b|\bkilla\b|\bkot\b/g,' ').replace(/[^a-z0-9]+/g,' ').trim();}
function photoMatches(title,name){
  const t=photoCoreName(String(title||'').replace(/^file:/i,''));
  const n=photoCoreName(name);
  if(!t||!n) return false;
  return n.split(/\s+/).filter(Boolean).every(tok=>t.includes(tok));
}
async function fetchOnlinePhoto(type,id,name){
  const special=specialDestinationPhoto(name);
  if(special?.url) return special.url;
  await loadPhotoRegistry();
  const asset=photoAsset(type,id);
  const query=asset?.photoApiQuery || `"${name}" Maharashtra`;
  const key=`spt_photo_v5_${type}_${id||photoCoreName(name)}`;
  const cached=localStorage.getItem(key); if(cached)return cached;
  try{
    const r=await fetch(`https://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=${encodeURIComponent(query)}&gsrnamespace=6&gsrlimit=12&prop=imageinfo&iiprop=url&iiurlwidth=1100&format=json&origin=*`);
    const j=await r.json(); const pages=Object.values(j.query?.pages||{});
    const exact=pages.find(x=>photoMatches(x.title,name));
    const url=exact?.imageinfo?.[0]?.thumburl||exact?.imageinfo?.[0]?.url;
    if(url){localStorage.setItem(key,url);return url;}
  }catch(e){console.warn('photo lookup',type,name,e)}
  return null;
}
async function hydrateFortPhotos(){
  const nodes=[...document.querySelectorAll('[data-fort-photo]')]; let i=0;
  const worker=async()=>{while(i<nodes.length){const n=nodes[i++];const special=specialDestinationPhoto(n.dataset.fortPhoto);const wiki=special?null:await fetchWikipediaDestination(n.dataset.fortPhoto);const url=special?.url||wiki?.image||await fetchOnlinePhoto('FORT',n.dataset.fortId,n.dataset.fortPhoto);if(url){n.innerHTML=`<img src="${esc(url)}" alt="${esc(n.dataset.fortPhoto)} fort" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">${special?.credit|| (wiki?.image?'Wikipedia':'Wikimedia Commons')}</span>`;}else{n.innerHTML=`<span>🏰</span><small>Photo pending verified match</small>`;}}};
  await Promise.all(Array.from({length:Math.min(3,nodes.length)},worker));
}
async function loadSpotPhotos(){
  const nodes=[...document.querySelectorAll('[data-spot-photo]')]; let i=0;
  const worker=async()=>{while(i<nodes.length){const n=nodes[i++];const special=specialDestinationPhoto(n.dataset.spotPhoto);const wiki=special?null:await fetchWikipediaDestination(n.dataset.spotPhoto);const url=special?.url||wiki?.image||await fetchOnlinePhoto('SPOT',n.dataset.spotId,n.dataset.spotPhoto);if(url){n.innerHTML=`<img src="${esc(url)}" alt="${esc(n.dataset.spotPhoto)}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">${special?.credit|| (wiki?.image?'Wikipedia':'Wikimedia Commons')}</span>`;}else{n.innerHTML=`<span>${n.dataset.icon||'🌿'}</span><small>Photo pending verified match</small>`;}}};
  await Promise.all(Array.from({length:Math.min(3,nodes.length)},worker));
}

const SPECIAL_WIKI_TITLES = {
  "trimbakeshwar jyotirlinga":"Trimbakeshwar Shiva Temple",
  "mallikarjuna jyotirlinga":"Srisailam Temple",
  "somnath jyotirlinga":"Somnath temple",
  "nageshwar jyotirlinga":"Nageshvara Jyotirlinga",
  "baidyanath jyotirlinga":"Baidyanath Temple",
  "bhimashankar jyotirlinga":"Bhimashankar Temple",
  "ramanathaswamy jyotirlinga":"Ramanathaswamy Temple",
  "kedarnath jyotirlinga":"Kedarnath Temple",
  "kashi vishwanath jyotirlinga":"Kashi Vishwanath Temple",
  "mahakaleshwar jyotirlinga":"Mahakaleshwar Jyotirlinga",
  "omkareshwar jyotirlinga":"Omkareshwar Temple",
  "grishneshwar jyotirlinga":"Grishneshwar Temple"
};
async function fetchWikipediaExactTitle(title, fallbackName){
  const key=`spt_wiki_exact_v2_${photoCoreName(title)}`;
  const cached=localStorage.getItem(key); if(cached){try{return JSON.parse(cached)}catch{}}
  try{
    const url=`https://en.wikipedia.org/w/api.php?action=query&prop=extracts|pageimages|info&titles=${encodeURIComponent(title)}&exintro=1&explaintext=1&exchars=1400&piprop=thumbnail&pilicense=any&pithumbsize=1200&inprop=url&format=json&origin=*`;
    const r=await fetch(url);
    if(r.ok){
      const j=await r.json(); const p=Object.values(j.query?.pages||{})[0];
      if(p && p.missing===undefined){
        const out={title:p.title||fallbackName,extract:p.extract||"",image:p.thumbnail?.source||null,url:p.fullurl||`https://en.wikipedia.org/wiki/${encodeURIComponent(p.title||title).replace(/%20/g,"_")}`};
        if(out.image||out.extract){localStorage.setItem(key,JSON.stringify(out));return out;}
      }
    }
  }catch(e){console.warn('Wikipedia exact lookup',title,e);}
  try{
    const rest=`https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(title.replace(/ /g,'_'))}`;
    const r=await fetch(rest);
    if(r.ok){const p=await r.json();const out={title:p.title||fallbackName,extract:p.extract||"",image:p.originalimage?.source||p.thumbnail?.source||null,url:p.content_urls?.desktop?.page||`https://en.wikipedia.org/wiki/${encodeURIComponent(title).replace(/%20/g,'_')}`};if(out.image||out.extract){localStorage.setItem(key,JSON.stringify(out));return out;}}
  }catch(e){console.warn('Wikipedia REST fallback',title,e);}
  return null;
}

async function fetchWikipediaDestination(name){
  const special=specialDestinationPhoto(name);
  if(special?.url) return {title:name,extract:"",image:special.url,url:special.source||"https://commons.wikimedia.org/"};
  const key=`spt_wiki_v4_${photoCoreName(name)}`;
  const cached=localStorage.getItem(key);
  if(cached){try{return JSON.parse(cached)}catch{}}
  try{
    const url=`https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrsearch=${encodeURIComponent(name)}&gsrnamespace=0&gsrlimit=8&prop=extracts|pageimages|info&exintro=1&explaintext=1&exchars=1200&piprop=thumbnail&pilicense=any&pithumbsize=1100&inprop=url&format=json&origin=*`;
    const r=await fetch(url); const j=await r.json();
    const pages=Object.values(j.query?.pages||{});
    const nameTokens=photoCoreName(name).split(/\s+/).filter(Boolean);
    const best=pages.find(p=>nameTokens.length && nameTokens.every(t=>photoCoreName(p.title).includes(t)));
    if(!best)return null;
    const out={title:best.title||name,extract:best.extract||"",image:best.thumbnail?.source||null,url:best.fullurl||`https://en.wikipedia.org/wiki/${encodeURIComponent(best.title||name).replace(/%20/g,"_")}`};
    localStorage.setItem(key,JSON.stringify(out)); return out;
  }catch(e){console.warn('Wikipedia lookup',name,e);return null;}
}
async function hydrateHomePhotos(){
  const nodes=[...document.querySelectorAll('[data-home-photo]')];
  let i=0;
  const worker=async()=>{while(i<nodes.length){
    const n=nodes[i++]; const name=n.dataset.homePhoto; const special=specialDestinationPhoto(name);
    const info=special?null:await fetchWikipediaDestination(name);
    const url=special?.url||info?.image||await fetchOnlinePhoto('SPOT',n.dataset.homeId,name);
    if(url){
      const inset=special?.secondaryUrl?`<img class="photo-inset" src="${esc(special.secondaryUrl)}" alt="${esc(name)} sunrise view" loading="lazy" referrerpolicy="no-referrer">`:"";
      n.innerHTML=`<div class="photo-dual"><img src="${esc(url)}" alt="${esc(name)}" loading="lazy" referrerpolicy="no-referrer">${inset}</div><span class="photo-credit">${esc(special?.credit||(info?.image?'Wikipedia':'Wikimedia Commons'))}</span>`;
    }
    else n.innerHTML=`<span>🌄</span><small>Photo pending verified match</small>`;
  }};
  await Promise.all(Array.from({length:Math.min(3,nodes.length)},worker));
}
async function hydrateDestinationInfo(containerId,name){
  const box=document.getElementById(containerId); if(!box)return;
  box.innerHTML='<div class="info-loading">Loading current destination information…</div>';
  const info=await fetchWikipediaDestination(name);
  if(!info){box.innerHTML='<p class="muted">Online destination information is currently unavailable.</p>';return;}
  box.innerHTML=`<span class="info-kicker">ONLINE DESTINATION GUIDE</span><h3>${esc(info.title||name)}</h3><p>${esc(info.extract||'Information pending.')}</p><a class="btn btn-outline btn-sm" href="${esc(info.url)}" target="_blank" rel="noopener noreferrer">Read source</a>`;
}
function detailItem(label,value){return `<div class="fort-detail-item"><span class="detail-label">${esc(label)}</span><b>${isReal(value)?esc(value):"PENDING"}</b></div>`;}
function fortFieldRows(f){
  const fields=[
    [fortT("district"),f.district],[fortT("locality"),f.locality],[fortT("baseVillage"),f.baseVillage],[fortT("coordinates"),isReal(f.latitude)&&isReal(f.longitude)?`${f.latitude}, ${f.longitude}`:null],
    [fortT("elevationLabel"),isReal(f.elevationM)?`${f.elevationM} m`:null],[fortT("bestSeason"),f.bestSeason],[fortT("fortType"),f.fortType],[fortT("protection"),f.protectionStatus],
    [fortT("parking"),f.parking],[fortT("water"),f.waterAvailability],[fortT("network"),f.networkAvailability],[fortT("monsoon"),f.monsoonRisk],
    [fortT("recordStatus"),f.recordStatus],[fortT("verificationStatus"),f.verificationStatus],[fortT("source"),f.primarySource],[fortT("locationSource"),f.locationSource],
    [fortT("locationConfidence"),f.locationConfidence],[fortT("verifiedDate"),f.locationVerifiedDate],[fortT("lastVerified"),f.lastVerified],[fortT("datasetScope"),f.datasetScope]
  ];
  return fields.map(([l,v])=>detailItem(l,v)).join("");
}
function routeDetail(r){
  const fields=[
    [fortT("route"),r.routeName],[fortT("routeStart"),r.routeStart],[fortT("routeEnd"),r.routeEnd],[fortT("distance"),isReal(r.routeDistanceKm)?`${r.routeDistanceKm} km`:null],
    [fortT("duration"),r.routeDuration],[fortT("difficulty"),r.routeDifficulty||r.difficulty],[fortT("routeType"),r.routeType],[fortT("routeSource"),r.routeSource],
    [fortT("routeVerification"),r.routeVerificationStatus],[fortT("gpsStatus"),r.gpsStatus],[fortT("trekStatus"),r.trekStatus],[fortT("routeConfidence"),r.routeConfidence],
    [fortT("trekStart"),r.trekStart],[fortT("trekRoute"),r.trekRoute],[fortT("trekDistance"),isReal(r.trekDistanceKm)?`${r.trekDistanceKm} km`:null],[fortT("trekDuration"),r.trekDuration],
    [fortT("coordinates"),isReal(r.routeStartLatitude)&&isReal(r.routeStartLongitude)?`${r.routeStartLatitude}, ${r.routeStartLongitude}`:null]
  ];
  return `<div class="fort-route-card"><div class="fort-route-head"><div><span class="settings-kicker">${fortT("route")}</span><h3>${esc(r.routeName||"Route")}</h3></div><span class="tag">${esc(r.routeDifficulty||r.difficulty||"PENDING")}</span></div><div class="fort-detail-grid">${fields.map(([l,v])=>detailItem(l,v)).join("")}</div></div>`;
}

async function openFortDetails(fortId){
  const fort=FORTS.find(f=>f.fortId===fortId); if(!fort)return;
  const hasGps=isReal(fort.latitude)&&isReal(fort.longitude);
  document.body.insertAdjacentHTML("beforeend",`<div class="modal-backdrop fort-modal-backdrop" id="fort-modal">
    <div class="modal fort-modal" role="dialog" aria-modal="true" aria-label="${esc(fort.fortName||"Fort")}">
      <div class="modal-head"><div><span class="settings-kicker">${fortVerified(fort)?fortT("verified"):fortT("registry")}</span><h2>${esc(fort.fortName||"Fort")}</h2><p class="muted">${esc(fort.district||"Maharashtra")}${fort.locality?` · ${esc(fort.locality)}`:""}</p></div><button class="close" onclick="closeFortModal()" aria-label="${fortT("close")}">×</button></div>
      <div class="fort-modal-photo live-detail-photo" id="fort-detail-photo"><span>🏰</span><small>Loading fort photo…</small></div>
      <div class="fort-quick-grid">${detailItem(fortT("district"),fort.district)}${detailItem(fortT("baseVillage"),fort.baseVillage)}${detailItem(fortT("elevationLabel"),isReal(fort.elevationM)?`${fort.elevationM} m`:null)}${detailItem(fortT("bestSeason"),fort.bestSeason)}</div>
      <div class="fort-action-row"><button class="btn btn-primary" onclick="navigateToFort('${esc(fort.fortId)}')">⌖ ${fortT("directions")}</button><button class="btn btn-secondary" onclick="openFortMap('${esc(fort.fortId)}')">◎ ${fortT("map")}</button><button class="btn btn-outline" onclick="checkFortFare('${esc(fort.fortId)}')">₹ Check Fare from My Location</button><button class="btn btn-outline" onclick="findTripToFort('${esc(fort.fortId)}')">🚐 Find Trips to this Fort</button></div><div id="fort-fare-box" class="fort-fare-box" hidden></div>
      <div class="fort-short-section"><span class="info-kicker">${fortT("overview")}</span><h3>${fortT("overview")}</h3><p class="fort-lead">${fortSummary(fort)}</p></div>
      <div class="fort-read-more-wrap"><button class="btn btn-outline fort-read-more" onclick="toggleFortFullDetails()">${fortT("readMore")}</button></div>
      <div id="fort-full-details" class="fort-full-details" hidden>
        <div class="fort-section-block"><span class="info-kicker">${fortT("location")}</span><h3>${fortT("location")}</h3><div class="fort-detail-grid">${fortFieldRows(fort)}</div></div>
        <div id="fort-route-preview" class="fort-section-block"><div class="empty">${fortT("loading")}</div></div>
      </div>
    </div>
  </div>`);
  const fortPhoto=document.getElementById("fort-detail-photo");
  const fortSpecial=specialDestinationPhoto(fort.fortName||"");
  const fortInfo=fortSpecial?null:await fetchWikipediaDestination(fort.fortName||"");
  const fortUrl=fortSpecial?.url||fortInfo?.image||await fetchOnlinePhoto("FORT",fort.fortId,fort.fortName||"");
  if(fortPhoto&&fortUrl) fortPhoto.innerHTML=`<img src="${esc(fortUrl)}" alt="${esc(fort.fortName||"Fort")}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">${esc(fortSpecial?.credit||(fortInfo?.image?"Wikipedia":"Wikimedia Commons"))}</span>`;
  if(fortInfo?.extract){const full=document.getElementById("fort-full-details");if(full){full.insertAdjacentHTML("afterbegin",`<div class="fort-section-block online-guide"><span class="info-kicker">ONLINE DESTINATION GUIDE</span><h3>Why ${esc(fort.fortName||"this fort")} is special</h3><p>${esc(fortInfo.extract)}</p><a class="btn btn-outline btn-sm" href="${esc(fortInfo.url)}" target="_blank" rel="noopener noreferrer">Read source</a></div>`);}}
  try{
    const routes=await API.get(`/api/forts/${encodeURIComponent(fortId)}/routes`);
    const el=document.getElementById("fort-route-preview"); if(!el)return;
    el.innerHTML=routes.length?`<span class="info-kicker">${fortT("routes")}</span><h3>${fortT("routes")}</h3>${routes.map(routeDetail).join("")}`:`<span class="info-kicker">${fortT("routes")}</span><h3>${fortT("routes")}</h3><p class="muted">${fortT("noRoute")}</p>`;
  }catch(error){const el=document.getElementById("fort-route-preview");if(el)el.innerHTML=`<span class="info-kicker">${fortT("routes")}</span><h3>${fortT("routes")}</h3><p class="muted">${fortT("routeUnavailable")}</p>`;}
}
function fortSummary(f){
  const parts=[];
  if(isReal(f.fortName)) parts.push(`<strong>${esc(f.fortName)}</strong>`);
  if(isReal(f.locality)) parts.push(`located around ${esc(f.locality)}`);
  if(isReal(f.baseVillage)) parts.push(`access/base village: ${esc(f.baseVillage)}`);
  if(isReal(f.elevationM)) parts.push(`elevation: ${esc(f.elevationM)} m`);
  if(isReal(f.bestSeason)) parts.push(`best season: ${esc(f.bestSeason)}`);
  return parts.join(" · ") || "Verified registry information is shown below. Fields not yet available are marked PENDING.";
}
function toggleFortFullDetails(){const el=document.getElementById("fort-full-details");const btn=document.querySelector(".fort-read-more");if(!el)return;const open=!el.hidden;el.hidden=open;if(btn)btn.textContent=open?fortT("readMore"):fortT("close");}
async function checkFortFare(fortId){
  const f=FORTS.find(x=>x.fortId===fortId),box=document.getElementById('fort-fare-box');
  if(!f||!box)return;
  await renderDestinationFare('fort-fare-box',f,f.fortName||f.locality||'Fort',f.fortName||f.locality||'');
}

function findTripToFort(fortId){const f=FORTS.find(x=>x.fortId===fortId);if(!f)return;closeFortModal();state.search.destination=f.fortName||f.locality||'';state.search.from='';go('trips');}
function closeFortModal(){document.getElementById("fort-modal")?.remove();}
async function openFortMap(fortId){const f=FORTS.find(x=>x.fortId===fortId);if(f)await openResolvedDestinationMap(f);}
async function navigateToFort(fortId){const f=FORTS.find(x=>x.fortId===fortId);if(f)await openResolvedDestinationDirections(f);}

// =====================================================
// TRIPS — live from GET /api/trips or /api/trips/search
// (backend requires an authenticated TRAVELER for this)
// =====================================================
function trips() {
  return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Find your next trip</h1><p class="section-sub">Live shared rides. Compare the vehicle, seats and final customer price before booking.</p></div></div><section class="section"><div class="container">
  <form class="search-card" style="box-shadow:none;border:1px solid var(--line);margin:0 0 25px" onsubmit="tripSearch(event)"><div class="field"><label>PICKUP</label><input id="trip-from" value="${esc(state.search.from || "")}" placeholder="Your location or Pune"></div><div class="field"><label>DESTINATION</label><input id="trip-dest" value="${esc(state.search.destination)}" placeholder="Rajgad Fort"></div><div class="field"><label>DATE</label><input id="trip-date" type="date" value="${esc(state.search.date)}"></div><div class="field"><label>PASSENGERS</label><input id="trip-pass" type="number" min="1" value="${state.search.passengers}"></div><div class="field"><label>TRIP TYPE</label><select id="trip-return"><option value="false" ${state.search.returnTrip ? "" : "selected"}>One way</option><option value="true" ${state.search.returnTrip ? "selected" : ""}>Return</option></select></div><div class="field"><label>VEHICLE</label><select id="trip-vehicle"><option value="">All vehicles</option>${VEHICLE_CATALOG.map(v => `<option value="${esc(v.vehicleType)}" ${state.search.vehicleType===v.vehicleType?"selected":""}>${esc(v.vehicleType)} · ${v.capacity} seats</option>`).join("")}</select></div><button class="btn btn-primary" type="submit">Find Trips</button></form>
  <div class="trip-results"><div id="trip-list">${loading("Loading live trips...")}</div><div><div class="panel"><h3>Route map</h3><div id="trip-route-map" class="live-map"><div class="empty">Enter pickup and destination, then search to see the route.</div></div></div><div class="panel" style="margin-top:18px"><h3>Customer pricing</h3><p class="muted">You see only the final fare per seat. Internal vehicle rate and platform margin are never shown.</p></div></div></div>
  </div></section></main>${footer()}`;
}

async function loadVehicleCatalog() {
  try { VEHICLE_CATALOG = await API.get("/api/trips/vehicles"); } catch (e) { console.warn("Vehicle catalog unavailable", e); VEHICLE_CATALOG = []; }
}

async function loadTrips() {
  const container = document.getElementById("trip-list");
  if (!container) return;
  try {
    const params = {
      from: state.search.from || undefined,
      to: state.search.destination || undefined,
      returnTrip: state.search.returnTrip || undefined,
      date: state.search.date || undefined,
      minSeats: state.search.passengers > 1 ? state.search.passengers : undefined
    };
    const hasFilters = params.from || params.to || params.date || params.minSeats || params.returnTrip;
    const endpoint = hasFilters ? `/api/trips/search${qs(params)}` : "/api/trips";
    const data = await API.get(endpoint);
    const vehicleFilter = state.search.vehicleType || "";
    TRIPS = (Array.isArray(data) ? data : []).map(t => ({
      id: t.tripId, source: t.fromLocation, destination: t.toLocation, date: t.travelDate, time: t.departureTime,
      totalSeats: t.totalSeats, seats: t.availableSeats, price: t.pricePerSeat, baseFare: t.baseTripFare,
      platformFee: t.platformFee, serviceFee: t.serviceFee || 69, totalTripFare: t.totalTripFare,
      distanceKm: t.distanceKm, returnTrip: !!t.returnTrip, vehicleType: t.vehicleType || "Vehicle", bookingMode: t.bookingMode || "SEAT_BASED", agencyId: t.agencyId,
      capacity: t.seatingCapacity || t.totalSeats, status: t.status
    }));
    TRIPS = vehicleFilter ? TRIPS.filter(t => t.vehicleType.toLowerCase().includes(vehicleFilter.toLowerCase())) : TRIPS;
    container.innerHTML = TRIPS.length ? TRIPS.map(tripCard).join("") : emptyBlock("No live trips found. Try another pickup, destination or clear filters.");
    renderTripRouteMap();
  } catch (error) {
    console.error("Failed to load trips:", error);
    container.innerHTML = errorBlock("Live trips could not be loaded right now. Please check that Spring Boot is running and refresh the page.");
  }
}

function tripCard(t) {
  const fromPrice = Number(t.price || 0).toFixed(0);
  const loggedIn = !!state.user;
  const traveler = state.user?.role === "TRAVELER";
  const cardAttrs = loggedIn ? "" : `role="button" tabindex="0" onclick="go('login')" onkeydown="if(event.key==='Enter'||event.key===' '){event.preventDefault();go('login')}"`;
  let action = "";
  if (loggedIn) {
    const bookButton = t.seats < 1
      ? `<button class="btn btn-secondary" disabled>Full</button>`
      : `<button class="btn btn-primary" onclick="event.stopPropagation();${traveler ? `openBooking(${t.id})` : `toast('A Traveler account is required to book a trip.')`}">Book Trip</button>`;
    action = `<div class="trip-action-buttons"><button class="btn btn-outline" onclick="event.stopPropagation();openTripDetails(${t.id})">View Details</button>${bookButton}</div>`;
  }
  const wholeVehicle = t.bookingMode === 'FULL_VEHICLE';
  const priceLabel = wholeVehicle ? `₹${Number(t.totalTripFare||0).toFixed(0)}` : `₹${fromPrice}<small class="muted"> / person</small>`;
  return `<article class="trip-card ${loggedIn ? 'trip-card-authenticated' : 'trip-card-public'}" ${cardAttrs}><div class="trip-card-image"><div class="vehicle-photo-icon">🚐</div></div><div class="trip-card-main"><div class="trip-card-top"><span class="tag">${t.status === "ACTIVE" ? "✓ Active" : esc(t.status)}</span><span class="tag">${t.returnTrip ? "Return" : "One Way"}</span>${wholeVehicle?'<span class="tag">Whole Vehicle</span>':''}</div><h3>${esc(t.source)} → ${esc(t.destination)}</h3><p class="muted">${esc(t.date)} · ${esc(t.time)}</p><div class="trip-vehicle"><strong>${esc(t.vehicleType)}</strong><span>${t.capacity} seat vehicle</span></div><div class="trip-meta"><span class="tag">${wholeVehicle?'Vehicle available':t.seats+' seats left'}</span></div></div><div class="trip-actions"><div class="price">${priceLabel}<div class="muted" style="font-size:12px;margin-top:4px">Final customer fare</div></div>${action}</div></article>`;
}

function openTripDetails(id) {
  if (!state.user) { go('login'); return; }
  const t = TRIPS.find(x => x.id === id);
  if (!t) { toast('Trip details are not available.'); return; }
  document.getElementById('trip-details-modal')?.remove();
  document.body.insertAdjacentHTML('beforeend', `<div class="modal-backdrop" id="trip-details-modal"><div class="modal trip-details-modal"><div class="modal-head"><div><span class="eyebrow page-eyebrow">TRIP DETAILS</span><h2>${esc(t.source)} → ${esc(t.destination)}</h2></div><button class="close" onclick="document.getElementById('trip-details-modal')?.remove()">×</button></div><div class="trip-details-grid"><div class="panel"><h3>Journey</h3>${tripDetailRow('Date',t.date)}${tripDetailRow('Departure',t.time)}${tripDetailRow('Trip type',t.returnTrip?'Return':'One Way')}${tripDetailRow('Available seats',t.seats)}${tripDetailRow('Vehicle',t.vehicleType)}${tripDetailRow('Vehicle capacity',`${t.capacity} seats`)}</div><div class="panel"><h3>Customer price</h3>${tripDetailRow(t.bookingMode==='FULL_VEHICLE'?'Whole vehicle fare':'Price / person',t.bookingMode==='FULL_VEHICLE'?`₹${Number(t.totalTripFare||0).toFixed(0)}`:`₹${Number(t.price||0).toFixed(0)}`)}${tripDetailRow('Service fee',`₹${Number(t.serviceFee||0).toFixed(0)}`)}${tripDetailRow('Route distance',t.distanceKm?`${Number(t.distanceKm).toFixed(1)} km`:'PENDING')}${tripDetailRow('Status',t.status||'PENDING')}<p class="muted trip-detail-note">Final customer fare is shown. Internal ₹/km rates and platform margin are never exposed.</p></div></div><div class="trip-details-actions"><button class="btn btn-outline" onclick="document.getElementById('trip-details-modal')?.remove()">Close</button>${t.seats>0 ? `<button class="btn btn-primary" onclick="document.getElementById('trip-details-modal')?.remove();${state.user?.role==='TRAVELER'?`openBooking(${t.id})`:`toast('A Traveler account is required to book a trip.')`}">Book Trip</button>` : ''}</div></div></div>`);
}
function tripDetailRow(label,value){ return `<div class="trip-detail-row"><span>${esc(label)}</span><b>${esc(value===null||value===undefined||String(value).trim()===''?'PENDING':value)}</b></div>`; }

async function geocodePlace(q){
  if(!q) return null;
  const r=await fetch(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&countrycodes=in&q=${encodeURIComponent(q)}`,{headers:{Accept:"application/json"}});
  if(!r.ok) throw new Error("Map search failed"); const d=await r.json(); return d[0]?{lat:Number(d[0].lat),lng:Number(d[0].lon),display:d[0].display_name}:null;
}
async function renderTripRouteMap(){
  const el=document.getElementById('trip-route-map'); if(!el)return;
  if(!state.search.from || !state.search.destination){return;}
  try{
    el.innerHTML=loading('Finding route…');
    const [a,b]=await Promise.all([geocodePlace(state.search.from),geocodePlace(state.search.destination)]);
    if(!a||!b){el.innerHTML=emptyBlock('Could not locate one of the places. Try a more specific location.');return;}
    const r=await fetch(`https://router.project-osrm.org/route/v1/driving/${a.lng},${a.lat};${b.lng},${b.lat}?overview=full&geometries=geojson`);
    const j=await r.json(); const route=j.routes?.[0]; if(!route)throw new Error('Route unavailable');
    el.innerHTML=`<div class="map-route-card"><div class="map-route-summary"><strong>${(route.distance/1000).toFixed(1)} km</strong><span>approx ${(route.duration/3600).toFixed(1)} hr</span></div><div class="map-route-line"><span>● ${esc(state.search.from)}</span><span>→</span><span>● ${esc(state.search.destination)}</span></div><small class="muted">Distance is used internally for route/fare logic; ₹/km is never shown to customers.</small></div>`;
  }catch(e){console.warn('route map',e);el.innerHTML=emptyBlock('Route preview is temporarily unavailable. Live trip results are still shown below.');}
}

function tripSearch(e) {
  e.preventDefault(); state.search.from=document.getElementById("trip-from").value.trim(); state.search.destination=document.getElementById("trip-dest").value.trim(); state.search.date=document.getElementById("trip-date").value; state.search.passengers=Number(document.getElementById("trip-pass").value||1); state.search.returnTrip=document.getElementById("trip-return")?.value==='true'; state.search.vehicleType=document.getElementById("trip-vehicle")?.value||""; loadTrips();
}

function searchSpot(n) { state.search.destination = n; go("trips") }
function homeSearch(e) { e.preventDefault(); state.search.destination = document.getElementById("home-dest").value; state.search.date = document.getElementById("home-date").value; state.search.passengers = Number(document.getElementById("home-pass").value || 1); go("trips") }

function showTripLoginPopup(tripId){
  document.getElementById('trip-login-modal')?.remove();
  document.body.insertAdjacentHTML('beforeend',`<div class="modal-backdrop" id="trip-login-modal"><div class="modal login-required-modal"><div class="modal-head"><div><span class="eyebrow page-eyebrow">BOOKING</span><h2>Login required</h2></div><button class="close" onclick="document.getElementById('trip-login-modal')?.remove()">×</button></div><div class="login-required-content"><div class="login-required-icon">🔐</div><h3>Login to book this trip</h3><p class="muted">You can browse available trips without logging in. To select seats and confirm a booking, please login as a Traveler.</p><div class="login-required-actions"><button class="btn btn-primary" onclick="document.getElementById('trip-login-modal')?.remove();go('login')">Login</button><button class="btn btn-outline" onclick="document.getElementById('trip-login-modal')?.remove();go('register')">Create Traveler Account</button></div></div></div></div>`);
}

function openBooking(id) {
  if (!state.user) { go('login'); return; }
  if (state.user.role !== "TRAVELER") { toast("A Traveler account is required to book a trip."); return; }
  const t = TRIPS.find(x => x.id === id);
  if (!t) return;
  const initialSeats = t.bookingMode === 'FULL_VEHICLE' ? Number(t.totalSeats) : 1;
  const initialFare = Number(t.price||0) * initialSeats;
  const initialTotal = initialFare + Number(t.serviceFee||0);
  document.body.insertAdjacentHTML("beforeend", `<div class="modal-backdrop" id="modal"><div class="modal"><div class="modal-head"><h2>${t.bookingMode==='FULL_VEHICLE'?'Book the whole vehicle':'Book your seats'}</h2><button class="close" onclick="closeModal()">×</button></div><p class="muted">${esc(t.source)} → ${esc(t.destination)} · ${esc(t.date)} · ${esc(t.time)}</p>${t.bookingMode==='FULL_VEHICLE'?'<div class="status orange" style="margin-top:10px">Whole vehicle booking · all published seats are reserved together.</div>':''}<div class="panel" style="margin-top:14px"><div style="display:flex;justify-content:space-between"><span>Vehicle</span><b>${esc(t.vehicleType)}</b></div><div style="display:flex;justify-content:space-between;margin-top:8px"><span>Route</span><b>${t.distanceKm ? Math.round(t.distanceKm) + ' km' : '—'} ${t.returnTrip ? '(return)' : '(one way)'}</b></div></div><div class="form-grid" style="margin-top:14px"><div class="field"><label>${t.bookingMode==='FULL_VEHICLE'?'BOOKING UNIT':'SEATS'}</label><input id="bk-seats" type="number" min="1" max="${t.seats}" value="${t.bookingMode==='FULL_VEHICLE'?t.totalSeats:1}" ${t.bookingMode==='FULL_VEHICLE'?'readonly':''} oninput="calcBooking(${t.price},${t.serviceFee})"></div></div><div class="panel" style="margin-top:18px"><div style="display:flex;justify-content:space-between"><span>${t.bookingMode==='FULL_VEHICLE'?'Vehicle fare / seat equivalent':'Price / person'}</span><b>₹${Number(t.price).toFixed(0)}</b></div><div style="display:flex;justify-content:space-between;margin-top:10px"><span>${t.bookingMode==='FULL_VEHICLE'?'Whole vehicle fare':'Selected seats'}</span><b id="bk-fare">₹${initialFare.toFixed(0)}</b></div><div style="display:flex;justify-content:space-between;margin-top:10px"><span>Sahyadri Service Fee</span><b>₹${Number(t.serviceFee).toFixed(0)}</b></div><div style="display:flex;justify-content:space-between;margin-top:12px;padding-top:12px;border-top:1px solid var(--line);font-size:18px"><span>Total</span><b id="bk-total">₹${initialTotal.toFixed(0)}</b></div></div><button class="btn btn-primary" style="width:100%;margin-top:18px" id="bk-confirm-btn" onclick="confirmBooking(${id})">Confirm booking</button></div></div>`);
}
function calcBooking(price, serviceFee) {
  const seats = Number(document.getElementById("bk-seats")?.value || 1);
  document.getElementById("bk-fare").textContent = money(price * seats);
  document.getElementById("bk-total").textContent = money(price * seats + Number(serviceFee || 0));
}
async function confirmBooking(id) {
  const trip = TRIPS.find(x => x.id === id);
  const seats = trip?.bookingMode === 'FULL_VEHICLE' ? Number(trip.totalSeats) : Number(document.getElementById("bk-seats")?.value || 1);
  const btn = document.getElementById("bk-confirm-btn");
  try {
    if (btn) { btn.disabled = true; btn.textContent = "Booking..."; }
    await API.post("/api/bookings", { tripId: id, seats });
    closeModal();
    toast("Booking confirmed!");
    loadTrips();
  } catch (error) {
    console.error("Booking error:", error);
    toast(error.message || "Booking failed.");
    if (btn) { btn.disabled = false; btn.textContent = "Confirm booking"; }
  }
}
function closeModal() { document.getElementById("modal")?.remove() }

// =====================================================
// MAHARASHTRA SPOTS — live from Spring Boot /api/spots
// Source: MAHARASHTRA_SPOTS_MASTER.csv. No mock spot records are used.
// =====================================================
let SPOTS = [];
window.SPOTS = SPOTS;
let spotSearchTerm = "";
let spotCategoryFilter = "";

function spots() {
  return `${header()}<main class="page"><div class="page-header"><div class="container">
    <span class="eyebrow page-eyebrow">SAHYADRI DESTINATIONS</span>
    <h1>Explore the Sahyadri</h1>
    <p class="section-sub">Waterfalls, beaches, hill stations, valleys, peaks, caves, sacred places, wildlife and special destinations across Maharashtra.</p>
  </div></div><section class="section"><div class="container">
    <div class="search-card spot-search-card" style="box-shadow:none;border:1px solid var(--line);margin:0 0 25px">
      <div class="field"><label>SEARCH</label><input id="spot-search" value="${esc(spotSearchTerm)}" oninput="filterSpots()" placeholder="Search name, district or locality…"></div>
      <div class="field"><label>CATEGORY</label><select id="spot-category" onchange="filterSpots()"><option value="">All Maharashtra categories</option>${[...new Set(SPOTS.map(s=>s.category).filter(Boolean))].sort().map(c=>`<option value="${esc(c)}" ${spotCategoryFilter===c?'selected':''}>${esc(c.replaceAll('_',' '))}</option>`).join('')}</select></div>
    </div>
    <div id="spot-list" class="grid grid-3">${loading("Loading Maharashtra spots from the backend…")}</div>
  </div></section></main>${footer()}`;
}

async function loadSpots() {
  const container=document.getElementById("spot-list");
  if(!container) return;
  try {
    const [spotData,destinationData]=await Promise.all([API.get("/api/spots"),API.get("/api/destinations")]);
    const base=Array.isArray(spotData)?spotData:[];
    // Bring every Maharashtra destination from the master registry into the Sahyadri section.
    // The backend registry remains untouched; this is a user-facing unified catalogue only.
    const maharashtraDestinations=(Array.isArray(destinationData)?destinationData:[])
      .filter(d=>String(d?.state||'').trim().toLowerCase()==='maharashtra')
      .map(d=>({
        spotId:`ND:${d.locationId}`, name:d.name, category:d.category||'NATURE', district:d.district, locality:d.locality,
        latitude:d.latitude, longitude:d.longitude, elevationM:d.elevationM, heightM:d.heightM, bestSeason:d.bestSeason,
        difficulty:d.difficulty, popularityTier:d.popularityTier, highlights:d.knownFor||d.description, primarySource:d.primarySource,
        verificationStatus:d.verificationStatus, lastVerified:d.lastVerified, recordStatus:d.recordStatus,
        __natureRecord:true, __sourceSection:d.section
      }));
    const seen=new Set();
    SPOTS=[...base,...maharashtraDestinations].filter(s=>{
      const key=String(s.name||'').trim().toLowerCase();
      if(!key||seen.has(key)) return false; seen.add(key); return true;
    });
    window.SPOTS=SPOTS;
    const category=document.getElementById("spot-category");
    if(category){category.innerHTML=`<option value="">All Maharashtra categories</option>${[...new Set(SPOTS.map(s=>s.category).filter(Boolean))].sort().map(c=>`<option value="${esc(c)}" ${spotCategoryFilter===c?'selected':''}>${esc(c.replaceAll('_',' '))}</option>`).join('')}`;}
    filterSpots();
  } catch(error) {
    console.error("Failed to load Sahyadri destinations:",error);
    container.innerHTML=errorBlock(error.message||"Unable to load Sahyadri destinations.");
  }
}

function filterSpots(){
  spotSearchTerm=document.getElementById("spot-search")?.value.trim()||"";
  spotCategoryFilter=document.getElementById("spot-category")?.value||"";
  const q=spotSearchTerm.toLowerCase();
  const matches=SPOTS.filter(s=>{
    const text=[s.name,s.category,s.district,s.locality,s.highlights].map(v=>String(v||"").toLowerCase());
    return (!q||text.some(v=>v.includes(q))) && (!spotCategoryFilter||String(s.category||"")===spotCategoryFilter);
  });
  const container=document.getElementById("spot-list");
  if(container){container.innerHTML=matches.length?matches.map(spotCard).join(""):emptyBlock("No Sahyadri destinations match your search."); if(matches.length) loadSpotPhotos();}
}

function spotCard(s){
  const category=String(s.category||"SPOT").replaceAll('_',' ');
  const icon={WATERFALL:"💧",BEACH:"🏖️",HILL_STATION:"⛰️",VALLEY:"🏞️",PEAK:"🗻",CLOUD_POINT:"☁️",ASHTAVINAYAK:"🛕",GHAT:"⛰️"}[s.category]||"🌿";
  return `<article class="card spot-card"><div class="spot-visual" data-spot-id="${esc(s.spotId||"")}" data-spot-photo="${esc(s.name||"")}" data-icon="${icon}"><span>${icon}</span><small>Loading photo…</small></div><div class="card-body"><div class="spot-tags"><span class="tag">${esc(category)}</span><span class="tag">${esc(s.popularityTier||"PENDING")}</span></div><h3>${esc(s.name)}</h3><p class="muted">${esc(s.locality||"PENDING")} · ${esc(s.district||"PENDING")}</p><p>${esc(s.highlights||"PENDING")}</p><div class="spot-meta"><span>${esc(s.difficulty||"PENDING")}</span><span>${esc(s.recordStatus||"PENDING")}</span></div><button class="btn btn-secondary btn-sm" onclick="openSpotDetails('${encodeURIComponent(s.spotId)}')">View Details</button></div></article>`;
}

function openSpotFromSearch(id){openSpotDetails(id);}
function spotValue(v){return v===null||v===undefined||String(v).trim()===""?"PENDING":String(v);}
function spotDetailRow(label,value){return `<div class="spot-detail-row"><span>${esc(label)}</span><b>${esc(spotValue(value))}</b></div>`;}

async function openSpotDetails(encodedId){
  const id=decodeURIComponent(encodedId||"");
  const s=SPOTS.find(x=>String(x.spotId)===id);
  if(!s){toast("Spot details are not available.");return;}
  const hasGps=Number.isFinite(Number(s.latitude))&&Number.isFinite(Number(s.longitude));
  document.body.insertAdjacentHTML("beforeend",`<div class="modal-backdrop" id="spot-modal"><div class="modal spot-modal"><div class="modal-head"><div><span class="eyebrow page-eyebrow">${esc(String(s.category||"SPOT").replaceAll('_',' '))}</span><h2>${esc(s.name)}</h2></div><button class="close" onclick="document.getElementById('spot-modal')?.remove()">×</button></div><div class="spot-modal-photo live-detail-photo" id="spot-detail-photo"><span>🌿</span><small>Loading destination photo…</small></div><div class="spot-detail-grid">
    <div class="panel"><h3>Overview</h3>${spotDetailRow("Category",String(s.category||"").replaceAll('_',' '))}${spotDetailRow("District",s.district)}${spotDetailRow("Locality",s.locality)}${spotDetailRow("Best season",s.bestSeason)}${spotDetailRow("Difficulty",s.difficulty)}${spotDetailRow("Popularity",s.popularityTier)}${spotDetailRow("Highlights",s.highlights)}</div>
    <div class="panel"><h3>Location & Data</h3>${spotDetailRow("Latitude",s.latitude)}${spotDetailRow("Longitude",s.longitude)}${spotDetailRow("Elevation (m)",s.elevationM)}${spotDetailRow("Height (m)",s.heightM)}${spotDetailRow("Primary source",s.primarySource)}${spotDetailRow("Verification status",s.verificationStatus)}${spotDetailRow("Last verified",s.lastVerified)}${spotDetailRow("Record status",s.recordStatus)}</div>
  </div><div id="spot-online-guide" class="online-guide panel"><span class="info-kicker">ONLINE DESTINATION GUIDE</span><h3>Why this place is special</h3><p>Loading current destination information…</p></div>${hasGps?`<div class="spot-actions"><button class="btn btn-primary" onclick="openSpotMap('${Number(s.latitude)}','${Number(s.longitude)}')">Open Location in Google Maps</button><button class="btn btn-outline" onclick="useLocationForSpot('${Number(s.latitude)}','${Number(s.longitude)}')">Get Directions From My Location</button></div>`:`<div class="note-box"><strong>Location note:</strong> Exact destination GPS is not verified yet. We will use the available locality/base-area reference for route and fare estimation; the road beyond that reference is not claimed as verified.</div>`}<div id="spot-fare-box" class="destination-fare-box" hidden></div><div class="spot-actions"><button class="btn btn-outline" onclick="checkSpotFare('${encodeURIComponent(s.spotId)}')">₹ Calculate My Rent</button><button class="btn btn-primary" onclick="findTripToDestination('${esc(s.name)}')">🚐 Find Trips & Book</button></div><p class="form-hint">This registry preserves the supplied verification status. Records marked DRAFT_FOR_REVIEW or AI-CURATED are not presented as officially verified.</p></div></div>`);
  const spotSpecial=specialDestinationPhoto(s.name||"");
  const spotInfo=spotSpecial?null:await fetchWikipediaDestination(s.name||"");
  const spotPhoto=document.getElementById("spot-detail-photo");
  const spotUrl=spotSpecial?.url||spotInfo?.image||await fetchOnlinePhoto("SPOT",s.spotId,s.name||"");
  if(spotPhoto&&spotUrl) spotPhoto.innerHTML=`<img src="${esc(spotUrl)}" alt="${esc(s.name)}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">${esc(spotSpecial?.credit||(spotInfo?.image?"Wikipedia":"Wikimedia Commons"))}</span>`;
  const guide=document.getElementById("spot-online-guide");
  if(guide&&spotInfo?.extract) guide.innerHTML=`<span class="info-kicker">ONLINE DESTINATION GUIDE</span><h3>Why ${esc(s.name)} is special</h3><p>${esc(spotInfo.extract)}</p><a class="btn btn-outline btn-sm" href="${esc(spotInfo.url)}" target="_blank" rel="noopener noreferrer">Read source</a>`;
}
const KNOWN_DESTINATION_COORDS={
  'Mallikarjuna Jyotirlinga':[16.0728,78.8684],
  'Somnath Jyotirlinga':[20.8880,70.4012],
  'Mahakaleshwar Jyotirlinga':[23.1828,75.7682],
  'Omkareshwar Jyotirlinga':[22.2414,76.1511],
  'Kedarnath Jyotirlinga':[30.7352,79.0669],
  'Bhimashankar Jyotirlinga':[19.0728,73.5340],
  'Kashi Vishwanath Jyotirlinga':[25.3109,83.0107],
  'Trimbakeshwar Jyotirlinga':[19.9320,73.5305],
  'Vaidyanath Jyotirlinga':[24.4900,86.6950],
  'Nageshwar Jyotirlinga':[22.3360,69.0577],
  'Ramanathaswamy Jyotirlinga':[9.2881,79.3174],
  'Grishneshwar Jyotirlinga':[20.0242,75.1780]
};
function validGeoPoint(lat,lng){
  const a=Number(lat),b=Number(lng);
  return Number.isFinite(a)&&Number.isFinite(b)&&a>=-90&&a<=90&&b>=-180&&b<=180&&!(Math.abs(a)<0.000001&&Math.abs(b)<0.000001);
}
function destinationIdentity(s){
  const name=String(s?.name||s?.fortName||s?.fort_name||'').trim();
  const state=String(s?.state||'Maharashtra').trim();
  const district=String(s?.district||'').trim();
  const locality=String(s?.locality||s?.baseVillage||s?.base_village||'').trim();
  return {name,state,district,locality};
}
function normalizeGeoText(v){return String(v||'').toLowerCase().normalize('NFKD').replace(/[\u0300-\u036f]/g,'').replace(/[^a-z0-9\s]/g,' ').replace(/\s+/g,' ').trim();}
function geoCacheKey(x){return `spt_geo_v3:${normalizeGeoText([x.name,x.locality,x.district,x.state].join('|'))}`;}
function readGeoCache(x){try{const raw=localStorage.getItem(geoCacheKey(x));if(!raw)return null;const v=JSON.parse(raw);return validGeoPoint(v?.lat,v?.lng)?v:null;}catch(e){return null;}}
function writeGeoCache(x,v){try{localStorage.setItem(geoCacheKey(x),JSON.stringify(v));}catch(e){}}
function scoreGeoCandidate(x,identity){
  const hay=normalizeGeoText([x.display_name,x.name,x.address?.city,x.address?.town,x.address?.village,x.address?.municipality,x.address?.county,x.address?.state].join(' '));
  const n=normalizeGeoText(identity.name), loc=normalizeGeoText(identity.locality), dist=normalizeGeoText(identity.district), state=normalizeGeoText(identity.state);
  let score=0;
  if(n&&hay.includes(n))score+=14;
  const tokens=n.split(' ').filter(t=>t.length>2);
  tokens.forEach(t=>{if(hay.includes(t))score+=2;});
  if(loc&&hay.includes(loc))score+=7;
  if(dist&&hay.includes(dist))score+=5;
  if(state&&hay.includes(state))score+=4;
  const type=normalizeGeoText(x.type||x.class||x.addresstype||x.properties?.type);
  if(/tourism|attraction|monument|castle|fort|peak|waterfall|beach|lake|park|temple|shrine|place|natural/.test(type))score+=2;
  score+=Math.min(Number(x.importance||0),1);
  return score;
}
async function fetchNominatimCandidates(q){
  const url=`https://nominatim.openstreetmap.org/search?format=jsonv2&addressdetails=1&limit=8&countrycodes=in&q=${encodeURIComponent(q)}`;
  const r=await fetch(url,{headers:{Accept:'application/json'}}); if(!r.ok)return [];
  const rows=await r.json(); return Array.isArray(rows)?rows:[];
}
async function fetchPhotonCandidates(q){
  const r=await fetch(`https://photon.komoot.io/api/?limit=8&q=${encodeURIComponent(q)}`); if(!r.ok)return [];
  const j=await r.json(); const rows=Array.isArray(j?.features)?j.features:[];
  return rows.map(f=>({lat:f?.geometry?.coordinates?.[1],lon:f?.geometry?.coordinates?.[0],display_name:[f?.properties?.name,f?.properties?.city,f?.properties?.state,'India'].filter(Boolean).join(', '),name:f?.properties?.name,address:f?.properties||{},type:f?.properties?.type,importance:f?.properties?.importance}));
}
async function fetchArcGisCandidates(q){
  const url=`https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates?f=json&maxLocations=8&countryCode=IND&singleLine=${encodeURIComponent(q)}`;
  const r=await fetch(url); if(!r.ok)return [];
  const j=await r.json(); const rows=Array.isArray(j?.candidates)?j.candidates:[];
  return rows.map(c=>({lat:c?.location?.y,lon:c?.location?.x,display_name:c?.address,name:c?.address,type:c?.attributes?.Addr_type,importance:c?.score/100}));
}
async function resolveDestinationTarget(s){
  const identity=destinationIdentity(s);
  const known=KNOWN_DESTINATION_COORDS[identity.name];
  if(Array.isArray(known)&&validGeoPoint(known[0],known[1])) return {lat:known[0],lng:known[1],exact:true,source:'curated landmark reference'};
  const directLat=s?.latitude ?? s?.lat;
  const directLng=s?.longitude ?? s?.lng ?? s?.lon;
  if(validGeoPoint(directLat,directLng)) return {lat:Number(directLat),lng:Number(directLng),exact:true,source:'registry'};
  const cached=readGeoCache(identity); if(cached)return cached;
  const {name,locality,district,state}=identity;
  if(!name)return null;
  const queries=[
    [name,locality,district,state,'India'],
    [name,district,state,'India'],
    [name,state,'India'],
    [name,'India']
  ].map(a=>a.filter(Boolean).join(', ')).filter((q,i,a)=>q&&!a.slice(0,i).includes(q));
  const providers=[fetchNominatimCandidates,fetchPhotonCandidates,fetchArcGisCandidates];
  for(const q of queries){
    for(const provider of providers){
      try{
        const rows=await provider(q);
        const scored=rows.filter(x=>String(x?.address?.country_code||'in').toLowerCase()==='in'||/\bindia\b/i.test(String(x?.display_name||''))).filter(x=>validGeoPoint(x?.lat,x?.lon)).map(x=>({x,score:scoreGeoCandidate(x,identity)})).sort((a,b)=>b.score-a.score);
        const best=scored[0];
        if(best && best.score>=8){
          const result={lat:Number(best.x.lat),lng:Number(best.x.lon),exact:false,display:best.x.display_name||q,source:'online geocoding'};
          writeGeoCache(identity,result); return result;
        }
      }catch(e){console.warn('destination geocode provider failed',provider.name,q,e);}
    }
  }
  return null;
}
function destinationGoogleSearchUrl(s){
  const x=destinationIdentity(s);
  return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent([x.name,x.locality,x.district,x.state,'India'].filter(Boolean).join(', '))}`;
}
async function openResolvedDestinationMap(s){
  const r=await resolveDestinationTarget(s);
  if(r&&validGeoPoint(r.lat,r.lng)){window.open(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${r.lat},${r.lng}`)}`,'_blank','noopener,noreferrer');return;}
  window.open(destinationGoogleSearchUrl(s),'_blank','noopener,noreferrer');
}
async function openResolvedDestinationDirections(s){
  const r=await resolveDestinationTarget(s);
  const destination=r&&validGeoPoint(r.lat,r.lng)?`${r.lat},${r.lng}`:[destinationIdentity(s).name,destinationIdentity(s).locality,destinationIdentity(s).district,destinationIdentity(s).state,'India'].filter(Boolean).join(', ');
  const open=(origin)=>{const p=new URLSearchParams({api:'1',destination,travelmode:'driving'});if(origin)p.set('origin',`${origin.latitude},${origin.longitude}`);window.open(`https://www.google.com/maps/dir/?${p.toString()}`,'_blank','noopener,noreferrer');};
  if(!navigator.geolocation){open(null);return;}
  navigator.geolocation.getCurrentPosition(pos=>open(pos.coords),()=>open(null),{enableHighAccuracy:true,timeout:10000,maximumAge:30000});
}

async function fetchRoadDistanceKm(fromLat,fromLng,toLat,toLng){
  const endpoints=[
    `https://router.project-osrm.org/route/v1/driving/${fromLng},${fromLat};${toLng},${toLat}?overview=false`,
    `https://routing.openstreetmap.de/routed-car/route/v1/driving/${fromLng},${fromLat};${toLng},${toLat}?overview=false`
  ];
  for(const url of endpoints){
    try{
      const controller=new AbortController(); const timer=setTimeout(()=>controller.abort(),12000);
      const r=await fetch(url,{signal:controller.signal}); clearTimeout(timer);
      if(!r.ok) continue; const j=await r.json(); const km=Number(j.routes?.[0]?.distance)/1000;
      if(Number.isFinite(km)&&km>0&&km<=4500) return {km,source:'road routing'};
    }catch(e){console.warn('road routing fallback',e);}
  }
  return null;
}
async function getUserPosition(){
  if(!navigator.geolocation) throw new Error('Location permission is required to calculate distance and fare.');
  return await new Promise((resolve,reject)=>navigator.geolocation.getCurrentPosition(resolve,reject,{enableHighAccuracy:true,timeout:12000,maximumAge:30000}));
}
async function renderDestinationFare(boxId,target,name,findName){
  const box=document.getElementById(boxId);if(!box)return;box.hidden=false;
  try{
    await loadVehicleCatalog();
    if(!Array.isArray(VEHICLE_CATALOG)||!VEHICLE_CATALOG.length) throw new Error('Vehicle catalogue is currently unavailable.');
    const options=VEHICLE_CATALOG.map(v=>`<option value="${esc(v.vehicleType)}">${esc(v.vehicleType)} · ${v.capacity} seats</option>`).join('');
    box.innerHTML=`<div class="fort-fare-head destination-fare-head"><div><span class="info-kicker">YOUR ROUTE</span><strong id="${boxId}-distance">Calculating…</strong><small id="${boxId}-distance-note">Getting your location and a reliable route to ${esc(name)}</small></div><div class="field"><label>VEHICLE</label><select id="${boxId}-vehicle">${options}</select></div><div class="field"><label>PASSENGERS</label><input id="${boxId}-seats" type="number" min="1" value="1"></div></div><div id="${boxId}-result" class="fare-preview"><span>Select vehicle and passengers. Fare will appear after the route distance is available.</span></div><small class="muted">Distance is calculated separately for this destination. Customer sees the final per-seat fare, not ₹/km.</small>`;
    const vehicleEl=document.getElementById(`${boxId}-vehicle`), seatsEl=document.getElementById(`${boxId}-seats`);
    let routeKm=null;
    const refreshFare=()=>{if(routeKm)quoteDestinationFare(boxId,routeKm,findName);};
    vehicleEl?.addEventListener('change',refreshFare); seatsEl?.addEventListener('change',refreshFare);
    const resolved=await resolveDestinationTarget(target);
    if(!resolved) throw new Error('A reliable map reference is not available for this destination yet.');
    const pos=await getUserPosition();
    const routed=await fetchRoadDistanceKm(pos.coords.latitude,pos.coords.longitude,resolved.lat,resolved.lng);
    if(routed){routeKm=routed.km;const d=document.getElementById(`${boxId}-distance`),n=document.getElementById(`${boxId}-distance-note`);if(d)d.textContent=`${routeKm.toFixed(1)} km`;if(n)n.innerHTML=`from your current location to ${esc(name)}${resolved.exact?'':' · approximate map reference — exact endpoint not verified'}`;refreshFare();return;}
    // Routing providers can temporarily rate-limit. For a mapped destination, use a clearly-labelled planning estimate rather than a false exact road distance.
    const straight=haversineKm(pos.coords.latitude,pos.coords.longitude,resolved.lat,resolved.lng);
    if(!Number.isFinite(straight)||straight<=0||straight>3500) throw new Error('Reliable road distance unavailable');
    routeKm=Math.min(straight*1.22,4500);
    const d=document.getElementById(`${boxId}-distance`),n=document.getElementById(`${boxId}-distance-note`);if(d)d.textContent=`≈ ${routeKm.toFixed(1)} km`;if(n)n.innerHTML=`planning estimate from your current location · live road routing is temporarily unavailable${resolved.exact?'':' · destination point is an approximate map reference'}`;refreshFare();
  }catch(e){box.innerHTML=emptyBlock(e.message||'Could not calculate distance right now. Please try again.');}
}
async function quoteDestinationFare(boxId,km,destinationName){const box=document.getElementById(`${boxId}-result`);const vehicle=document.getElementById(`${boxId}-vehicle`)?.value;const seats=Math.max(1,Number(document.getElementById(`${boxId}-seats`)?.value||1));if(!box||!vehicle||!Number.isFinite(Number(km)))return;try{const q=await API.get(`/api/trips/pricing/quote${qs({vehicleType:vehicle,seats,distanceKm:km,returnTrip:false})}`);box.innerHTML=`<strong>Estimated fare</strong><b>${money(q.pricePerPerson||0)} / seat</b><span>One way · ${esc(vehicle)} · ${seats} passenger${seats>1?'s':''}</span><button class="btn btn-primary btn-sm" onclick="findTripToDestination('${esc(destinationName)}')">Continue to Find Trips & Book</button>`;}catch(e){box.innerHTML=emptyBlock(e.message||'Fare unavailable.');}}
async function checkSpotFare(encodedId){const id=decodeURIComponent(encodedId||'');const s=SPOTS.find(x=>String(x.spotId)===id);if(!s)return;await renderDestinationFare('spot-fare-box',s,s.name||s.locality||'Destination',s.name||s.locality||'');}
function findTripToDestination(name){if(!name)return;document.getElementById('spot-modal')?.remove();document.getElementById('nature-modal')?.remove();state.search.destination=name;state.search.from='';go('trips');}
function openSpotMap(lat,lng){window.open(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${lat},${lng}`)}`,'_blank','noopener');}
function useLocationForSpot(lat,lng){
  if(!navigator.geolocation){openSpotMap(lat,lng);return;}
  navigator.geolocation.getCurrentPosition(pos=>{window.open(`https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(`${pos.coords.latitude},${pos.coords.longitude}`)}&destination=${encodeURIComponent(`${lat},${lng}`)}`,'_blank','noopener');},()=>{toast("Location permission was not available. Opening the spot location instead.");openSpotMap(lat,lng);},{enableHighAccuracy:true,timeout:10000});
}

// =====================================================
// STAYS — live from GET /api/properties or /api/properties/search
// =====================================================
function stays() { const today=new Date().toISOString().slice(0,10); const t=new Date(Date.now()+86400000).toISOString().slice(0,10); const q=state.staySearch||{}; setTimeout(loadProperties,0); return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Stay close to the adventure</h1><p class="section-sub">Search only approved registered hotels and homestays with live room availability.</p></div></div><section class="section"><div class="container"><form onsubmit="staySearch(event)" class="search-bar"><div class="field"><label>LOCATION / BASE VILLAGE</label><input id="stay-loc" value="${esc(q.location||'')}" required placeholder="Raigad, Gunjavane, Bhandardara..."></div><div class="field"><label>CHECK-IN</label><input id="stay-in" type="date" min="${today}" value="${esc(q.checkIn||today)}" required></div><div class="field"><label>CHECK-OUT</label><input id="stay-out" type="date" min="${t}" value="${esc(q.checkOut||t)}" required></div><button class="btn btn-primary" type="submit">Search</button></form><div id="stay-results" class="grid cards-3" style="margin-top:26px">${loading('Finding approved stays...')}</div></div></section></main>${footer()}`; }
async function loadProperties(){const c=document.getElementById('stay-results'); if(!c)return; try{const q=state.staySearch||{}; const data=await API.get(`/api/properties/search${qs({location:q.location||null,checkInDate:q.checkIn||null,checkOutDate:q.checkOut||null,minRooms:1})}`); c.innerHTML=data.length?data.map(stayCard).join(''):emptyBlock('No approved registered stays found for this location and dates.');}catch(e){c.innerHTML=errorBlock(e.message||'Unable to load stays.');}}
function stayCard(h){const img=h.photoUrl||'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=900&q=80'; return `<div class="card"><img class="place-img" src="${esc(img)}" alt="${esc(h.propertyName)}"><div class="card-body"><span class="tag">Verified ${esc(h.propertyType)}</span><h3>${esc(h.propertyName)}</h3><p class="muted">📍 ${esc(h.location)}</p><p class="muted">${h.availableRooms}/${h.totalRooms} rooms available</p><p class="muted">Check-in ${esc(h.checkInTime||'12:00 PM')} · Check-out ${esc(h.checkOutTime||'10:00 AM')}</p><div style="display:flex;justify-content:space-between;align-items:center;gap:10px"><span class="price">${money(h.pricePerNight)}<small class="muted"> / night</small></span><button class="btn btn-primary btn-sm" onclick="bookStay(${h.propertyId})">Book stay</button></div></div></div>`;}
function staySearch(e){e.preventDefault(); const i=document.getElementById('stay-in').value,o=document.getElementById('stay-out').value;if(!o||!i||o<=i){toast('Choose a check-out date after check-in.');return;} state.staySearch={location:document.getElementById('stay-loc').value.trim(),checkIn:i,checkOut:o}; render();}
async function bookStay(id){if(!state.user)return loginPrompt('Login to book your stay.'); const props=await API.get(`/api/properties/search${qs({location:state.staySearch.location,checkInDate:state.staySearch.checkIn,checkOutDate:state.staySearch.checkOut,minRooms:1})}`); const h=props.find(x=>x.propertyId===id); if(!h){toast('Stay is no longer available.');return;} const nights=Math.max(1,(new Date(state.staySearch.checkOut)-new Date(state.staySearch.checkIn))/86400000); document.body.insertAdjacentHTML('beforeend',`<div class="modal-backdrop" id="modal"><div class="modal" style="max-width:720px"><div class="modal-head"><h2>${esc(h.propertyName)}</h2><button class="close" onclick="closeModal()">×</button></div><div class="stay-booking-steps"><div class="step active">1 Dates</div><div class="step active">2 Room</div><div class="step">3 Payment</div></div><div class="booking-summary"><p><b>📍 ${esc(h.location)}</b></p><div class="form-grid"><div class="field"><label>Check-in</label><input id="pb-in" type="date" value="${state.staySearch.checkIn}" readonly></div><div class="field"><label>Check-out</label><input id="pb-out" type="date" value="${state.staySearch.checkOut}" readonly></div><div class="field"><label>Rooms</label><input id="pb-rooms" type="number" value="1" min="1" max="${h.availableRooms}" oninput="stayTotalPreview(${h.propertyId},${nights},${h.pricePerNight},${h.availableRooms})"></div><div class="field"><label>Payment</label><select id="pb-pay" onchange="stayTotalPreview(${h.propertyId},${nights},${h.pricePerNight},${h.availableRooms})"><option value="PAY_AT_HOTEL">Pay at Hotel</option><option value="ONLINE">Pay Online</option></select></div><div class="field full"><label>Coupon code (optional)</label><div style="display:flex;gap:8px"><input id="pb-coupon" placeholder="Enter Sahyadri coupon"><button type="button" class="btn btn-outline" onclick="stayTotalPreview(${h.propertyId},${nights},${h.pricePerNight},${h.availableRooms},true)">Apply</button></div></div></div><div class="panel" style="margin-top:14px"><div id="pb-total"></div><p class="muted">Easy stay schedule: arrive after <b>${esc(h.checkInTime||'12:00 PM')}</b> and leave before <b>${esc(h.checkOutTime||'10:00 AM')}</b>.</p></div><button class="btn btn-primary" style="width:100%;margin-top:12px" id="pb-confirm-btn" onclick="confirmStayBooking(${h.propertyId},${nights},${h.pricePerNight})">Confirm Stay Booking</button></div></div></div>`); stayTotalPreview(h.propertyId,nights,h.pricePerNight,h.availableRooms);}
function stayTotalPreview(id,nights,price,maxRooms,apply){const rooms=Math.max(1,Math.min(maxRooms,Number(document.getElementById('pb-rooms')?.value||1))); document.getElementById('pb-rooms').value=rooms; const gross=rooms*nights*price; const coupon=(document.getElementById('pb-coupon')?.value||'').trim(); const pay=document.getElementById('pb-pay')?.value||'PAY_AT_HOTEL'; const total=document.getElementById('pb-total'); total.innerHTML=`<div style="display:flex;justify-content:space-between"><span>${rooms} room × ${nights} night</span><b>${money(gross)}</b></div><div style="display:flex;justify-content:space-between;margin-top:8px"><span>Coupon</span><b>${coupon?esc(coupon):'Not applied'}</b></div><hr style="margin:12px 0"><div style="display:flex;justify-content:space-between;font-size:1.08rem"><b>Estimated total</b><b>${money(gross)}</b></div><p class="muted" style="margin:8px 0 0">${pay==='ONLINE'?'Online payment will be completed through Razorpay checkout.':'Pay the hotel directly at check-in.'}</p>`;}
async function confirmStayBooking(id,nights,price){const btn=document.getElementById('pb-confirm-btn'); const body={propertyId:id,rooms:Number(document.getElementById('pb-rooms').value||1),checkInDate:document.getElementById('pb-in').value,checkOutDate:document.getElementById('pb-out').value,paymentMethod:document.getElementById('pb-pay').value,couponCode:document.getElementById('pb-coupon').value.trim()}; try{btn.disabled=true;btn.textContent='Confirming...';const b=await API.post('/api/property-bookings',body); closeModal(); toast(`Stay confirmed · ${money(b.totalAmount)}`); loadProperties();}catch(e){toast(e.message||'Booking failed.');btn.disabled=false;btn.textContent='Confirm Stay Booking';}}

// =====================================================
// CREATE TRIP (Driver) — POST /api/trips
// =====================================================
function createTrip() {
  return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Create a new trip</h1><p class="section-sub">Your verified vehicle is used automatically. Set the route and passenger seats; the customer fare is calculated server-side.</p></div></div><section class="section"><div class="container"><form onsubmit="createTripSubmit(event)" class="dashboard-grid"><div class="panel"><h3>Route details</h3><div class="form-grid"><div class="field"><label>SOURCE</label><input id="ct-source" required placeholder="Pune"></div><div class="field"><label>DESTINATION</label><input id="ct-dest" required placeholder="Rajgad Fort"></div><div class="field"><label>DATE</label><input id="ct-date" required type="date"></div><div class="field"><label>TIME</label><input id="ct-time" required type="time" value="06:00"></div><div class="field full"><label>ONE-WAY ROUTE DISTANCE (KM)</label><input id="ct-distance" type="number" min="1" step="0.1" placeholder="Calculated/confirmed route distance" required><small class="muted">Used only for internal fare calculation. Customers will not see ₹/km.</small></div></div></div><div class="panel"><h3>Trip settings & fare preview</h3><div class="form-grid"><div class="field"><label>VERIFIED VEHICLE</label><input id="ct-vehicle" value="Loading verified vehicle…" readonly></div><div class="field"><label>PASSENGER SEATS</label><input id="ct-seats" type="number" min="1" value="4" required oninput="previewTripFare()"></div><div class="field"><label>TRIP TYPE</label><select id="ct-return" onchange="previewTripFare()"><option value="false">One way</option><option value="true">Return</option></select></div></div><div id="ct-fare-preview" class="fare-preview"><strong>Fare preview</strong><span>Enter route distance and seats.</span></div><button class="btn btn-primary" style="width:100%;margin-top:18px" id="ct-submit-btn">Publish Trip</button></div></form></div></section></main>`;
}
async function loadDriverVehicleForTrip(){try{const v=await API.get('/api/driver/vehicle');window.SPT_DRIVER_VEHICLE=v;const el=document.getElementById('ct-vehicle');if(el)el.value=`${v.vehicleType} · ${v.seatingCapacity} seats`;const seats=document.getElementById('ct-seats');if(seats){seats.max=v.seatingCapacity;seats.value=Math.min(Number(seats.value||v.seatingCapacity),v.seatingCapacity);}previewTripFare();}catch(e){const el=document.getElementById('ct-vehicle');if(el)el.value='Vehicle verification required';toast(e.message||'Could not load verified vehicle.');}}
async function previewTripFare(){
  const d=Number(document.getElementById('ct-distance')?.value||0), seats=Number(document.getElementById('ct-seats')?.value||0), ret=document.getElementById('ct-return')?.value==='true', el=document.getElementById('ct-fare-preview');
  if(!el)return; if(!d||!seats){el.innerHTML='<strong>Fare preview</strong><span>Enter route distance and seats.</span>';return;}
  try{const vehicleType=window.SPT_DRIVER_VEHICLE?.vehicleType||''; if(!vehicleType){el.innerHTML='<strong>Fare preview</strong><span>Verified vehicle is not available yet.</span>';return;} const q=await API.get(`/api/trips/pricing/quote${qs({vehicleType,seats,distanceKm:d,returnTrip:ret})}`); el.innerHTML=`<strong>Customer fare preview</strong><b>${money(q.pricePerPerson||0)} / person</b><span>${ret?'Return trip':'One way'} · service fee ${money(q.serviceFee||69)} per booking</span>`;}catch(e){el.innerHTML='<strong>Fare preview</strong><span>Verified vehicle pricing will be applied when the trip is published.</span>';}
}

async function createTripSubmit(e) {
  e.preventDefault();
  const btn = document.getElementById("ct-submit-btn");

  const body = {
    fromLocation: document.getElementById("ct-source").value.trim(),
    toLocation: document.getElementById("ct-dest").value.trim(),
    travelDate: document.getElementById("ct-date").value,
    departureTime: document.getElementById("ct-time").value,
    totalSeats: Number(document.getElementById("ct-seats").value),
    distanceKm: Number(document.getElementById("ct-distance").value),
    returnTrip: document.getElementById("ct-return").value === "true"
  };

  try {
    if (btn) { btn.disabled = true; btn.textContent = "Publishing..."; }
    await API.post("/api/trips", body);
    toast("Trip published successfully.");
    go("dashboard");
  } catch (error) {
    console.error("Create trip error:", error);
    toast(error.message || "Could not publish trip.");
    if (btn) { btn.disabled = false; btn.textContent = "Publish Trip"; }
  }
}

async function cancelTrip(id) {
  if (!confirm("Cancel this trip? All confirmed bookings on it will also be cancelled.")) return;
  try {
    await API.delete(`/api/trips/${id}`);
    toast("Trip cancelled.");
    loadDriverDashboard();
  } catch (error) {
    toast(error.message || "Could not cancel trip.");
  }
}

async function driverCancelBooking(id) {
  if (!confirm("Cancel this passenger's booking?")) return;
  try {
    await API.delete(`/api/bookings/driver/${id}`);
    toast("Booking cancelled.");
    loadDriverDashboard();
  } catch (error) {
    toast(error.message || "Could not cancel booking.");
  }
}

// =====================================================
// CREATE PROPERTY (Hotel Owner) — POST /api/properties
// =====================================================
function createProperty() { return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>List a new property</h1><p class="section-sub">Publish your approved hotel or homestay with rooms, photos and traveler-friendly stay details.</p></div></div><section class="section"><div class="container"><form onsubmit="createPropertySubmit(event)" class="panel" style="max-width:820px;margin:auto"><div class="form-grid"><div class="field full"><label>PROPERTY NAME</label><input id="cp-name" required placeholder="Rajgad Valley Homestay"></div><div class="field"><label>TYPE</label><select id="cp-type"><option value="HOTEL">Hotel</option><option value="HOMESTAY">Homestay</option></select></div><div class="field"><label>BASE VILLAGE / LOCATION</label><input id="cp-location" required placeholder="Gunjavane"></div><div class="field"><label>TOTAL ROOMS</label><input id="cp-rooms" type="number" min="1" value="5" required></div><div class="field"><label>PRICE / NIGHT (₹)</label><input id="cp-price" type="number" min="1" value="1200" required></div><div class="field"><label>CHECK-IN</label><input id="cp-checkin" value="12:00 PM" required></div><div class="field"><label>CHECK-OUT</label><input id="cp-checkout" value="10:00 AM" required></div><div class="field"><label>PAYMENT OPTIONS</label><select id="cp-payment"><option value="BOTH">Online + Pay at Hotel</option><option value="ONLINE">Online Only</option><option value="PAY_AT_HOTEL">Pay at Hotel Only</option></select></div><div class="field full"><label>PROPERTY PHOTO URL</label><input id="cp-photo" placeholder="https://... image link"></div><div class="field"><label>SAHYADRI COUPON CODE</label><input id="cp-coupon" placeholder="Optional"></div><div class="field"><label>DISCOUNT (₹)</label><input id="cp-discount" type="number" min="0" value="0"></div><div class="field"><label>MIN BOOKING FOR COUPON (₹)</label><input id="cp-mincoupon" type="number" min="0" value="0"></div><div class="field full"><label>DESCRIPTION</label><textarea id="cp-desc" rows="4" placeholder="Tell travelers about your stay, food, parking and trek access..."></textarea></div></div><button class="btn btn-primary" style="width:100%;margin-top:15px" id="cp-submit-btn">Publish Property</button></form></div></section></main>` }
async function createPropertySubmit(e) {
  e.preventDefault();
  const btn = document.getElementById("cp-submit-btn");

  const body = {
    propertyName: document.getElementById("cp-name").value.trim(),
    propertyType: document.getElementById("cp-type").value,
    location: document.getElementById("cp-location").value.trim(),
    description: document.getElementById("cp-desc").value.trim(),
    totalRooms: Number(document.getElementById("cp-rooms").value),
    pricePerNight: Number(document.getElementById("cp-price").value), photoUrl: document.getElementById("cp-photo").value.trim(), checkInTime: document.getElementById("cp-checkin").value.trim(), checkOutTime: document.getElementById("cp-checkout").value.trim(), paymentMode: document.getElementById("cp-payment").value, couponCode: document.getElementById("cp-coupon").value.trim(), couponDiscount: Number(document.getElementById("cp-discount").value||0), couponMinAmount: Number(document.getElementById("cp-mincoupon").value||0)
  };

  try {
    if (btn) { btn.disabled = true; btn.textContent = "Listing..."; }
    await API.post("/api/properties", body);
    toast("Property listed successfully.");
    go("dashboard");
  } catch (error) {
    console.error("Create property error:", error);
    toast(error.message || "Could not list property.");
    if (btn) { btn.disabled = false; btn.textContent = "List Property"; }
  }
}

async function cancelProperty(id) {
  if (!confirm("Deactivate this property? It will no longer be bookable.")) return;
  try {
    await API.delete(`/api/properties/${id}`);
    toast("Property deactivated.");
    loadHotelDashboard();
  } catch (error) {
    toast(error.message || "Could not deactivate property.");
  }
}

// =====================================================
// DASHBOARDS — shell renders instantly, real data loads async
// =====================================================
function dashboard() {
  if (!state.user) return login();
  const r = state.user.role;
  if (r === "DRIVER") return driverDash();
  if (r === "AGENCY") return agencyDash();
  if (r === "HOTEL_OWNER") return hotelDash();
  if (r === "ADMIN") return adminDash();
  return travelerDash();
}
function dashShell(title, subtitle, content) { return `${header()}<main class="dashboard-layout"><aside class="sidebar"><div class="side-title">Workspace</div><a class="side-link active" href="#dashboard">⌂ Dashboard</a><a class="side-link" href="#trips">Find Trips</a><a class="side-link" href="#stays">Stays</a><a class="side-link" href="#return">Return Trips</a><div class="side-title">Account</div><a class="side-link" href="#profile">Profile</a><a class="side-link" href="#safety">Safety & SOS</a><a class="side-link" href="#" onclick="logout()">Logout</a></aside><section class="dashboard-main"><div class="dash-head"><div><h1>${title}</h1><p class="muted">${subtitle}</p></div></div>${content}</section></main>` }

// ---------- TRAVELER ----------
function travelerDash() {
  return dashShell("Traveler Dashboard", "Your live trips, stays and bookings.", `
  <div class="stat-grid" id="trav-stats"><div class="stat">Trip bookings<div class="number" id="stat-trip-bk">–</div></div><div class="stat">Stay bookings<div class="number" id="stat-stay-bk">–</div></div><div class="stat">Confirmed trips<div class="number" id="stat-trip-conf">–</div></div><div class="stat">Confirmed stays<div class="number" id="stat-stay-conf">–</div></div></div>
  <div class="dashboard-grid"><div class="panel"><h3>My Trip Bookings</h3><div id="my-trip-bookings">${loading("Loading...")}</div></div><div class="panel"><h3>My Stay Bookings</h3><div id="my-stay-bookings">${loading("Loading...")}</div></div></div>`);
}

async function loadTravelerDashboard() {
  try {
    const [tripBookings, stayBookings] = await Promise.all([
      API.get("/api/bookings/my"),
      API.get("/api/property-bookings/my")
    ]);

    const confTrips = tripBookings.filter(b => b.status === "CONFIRMED").length;
    const confStays = stayBookings.filter(b => b.status === "CONFIRMED").length;

    setText("stat-trip-bk", tripBookings.length);
    setText("stat-stay-bk", stayBookings.length);
    setText("stat-trip-conf", confTrips);
    setText("stat-stay-conf", confStays);

    const tripEl = document.getElementById("my-trip-bookings");
    tripEl.innerHTML = tripBookings.length ? tripBookings.map(b => `
      <div class="trip-card" style="grid-template-columns:1fr auto"><div><span class="status ${b.status === "CONFIRMED" ? "green" : "red"}">${esc(b.status)}</span><h3>${esc(b.fromLocation)} → ${esc(b.toLocation)}</h3><p class="muted"> ${esc(b.travelDate)} ·  ${esc(b.departureTime)} ·  ${esc(b.driverName)}</p><p class="muted">${b.seatsBooked} seat(s) · ${money(b.totalAmount)}</p></div><div class="trip-actions">${b.status === "CONFIRMED" ? `<button class="btn btn-danger btn-sm" onclick="cancelTripBooking(${b.bookingId})">Cancel</button><button class="btn btn-outline btn-sm" onclick="openReviewModal('TRIP', ${b.tripId})">Leave review</button>` : ""}</div></div>`).join("") : emptyBlock("No trip bookings yet. Go find a trip!");

    const stayEl = document.getElementById("my-stay-bookings");
    stayEl.innerHTML = stayBookings.length ? stayBookings.map(b => `
      <div class="trip-card" style="grid-template-columns:1fr auto"><div><span class="status ${b.status === "CONFIRMED" ? "green" : "red"}">${esc(b.status)}</span><h3>${esc(b.propertyName)}</h3><p class="muted"> ${esc(b.location)} · ${esc(b.checkInDate)} → ${esc(b.checkOutDate)}</p><p class="muted">${b.roomsBooked} room(s) · ${money(b.totalAmount)}</p></div><div class="trip-actions">${b.status === "CONFIRMED" ? `<button class="btn btn-danger btn-sm" onclick="cancelStayBooking(${b.bookingId})">Cancel</button><button class="btn btn-outline btn-sm" onclick="openReviewModal('PROPERTY', ${b.propertyId})">Leave review</button>` : ""}</div></div>`).join("") : emptyBlock("No stay bookings yet.");

  } catch (error) {
    console.error("Dashboard load error:", error);
    ["my-trip-bookings", "my-stay-bookings"].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.innerHTML = errorBlock(error.message || "Could not load your bookings.");
    });
  }
}

async function cancelTripBooking(id) {
  if (!confirm("Cancel this booking?")) return;
  try { await API.delete(`/api/bookings/${id}`); toast("Booking cancelled."); loadTravelerDashboard(); }
  catch (error) { toast(error.message || "Could not cancel booking."); }
}
async function cancelStayBooking(id) {
  if (!confirm("Cancel this stay booking?")) return;
  try { await API.delete(`/api/property-bookings/${id}`); toast("Booking cancelled."); loadTravelerDashboard(); }
  catch (error) { toast(error.message || "Could not cancel booking."); }
}

function openReviewModal(reviewType, targetId) {
  document.body.insertAdjacentHTML("beforeend", `<div class="modal-backdrop" id="modal"><div class="modal"><div class="modal-head"><h2>Leave a review</h2><button class="close" onclick="closeModal()">×</button></div><div class="field"><label>RATING (1-5)</label><input id="rv-rating" type="number" min="1" max="5" value="5"></div><div class="field" style="margin-top:12px"><label>COMMENT</label><textarea id="rv-comment" rows="4" placeholder="Share your experience..."></textarea></div><button class="btn btn-primary" style="width:100%;margin-top:16px" id="rv-submit-btn" onclick="submitReview('${reviewType}', ${targetId})">Submit review</button></div></div>`);
}
async function submitReview(reviewType, targetId) {
  const rating = Number(document.getElementById("rv-rating").value);
  const comment = document.getElementById("rv-comment").value.trim();
  const btn = document.getElementById("rv-submit-btn");
  try {
    if (btn) { btn.disabled = true; btn.textContent = "Submitting..."; }
    await API.post("/api/reviews", { reviewType, targetId, rating, comment });
    closeModal();
    toast("Review submitted. Thank you!");
  } catch (error) {
    toast(error.message || "Could not submit review.");
    if (btn) { btn.disabled = false; btn.textContent = "Submit review"; }
  }
}

// ---------- DRIVER ----------
function driverDash() {
  return dashShell("Driver Dashboard", "Manage your live trips and passenger bookings.", `
  <div class="stat-grid"><div class="stat">My trips<div class="number" id="stat-my-trips">–</div></div><div class="stat">Active trips<div class="number" id="stat-active-trips">–</div></div><div class="stat">Total bookings<div class="number" id="stat-driver-bookings">–</div></div><div class="stat">Revenue<div class="number" id="stat-driver-revenue">–</div></div></div>
  <div class="dashboard-grid"><div class="panel"><div class="dash-head"><h3>My trips</h3><button class="btn btn-primary btn-sm" onclick="go('create-trip')">+ Create Trip</button></div><div class="table-wrap" id="driver-trips">${loading("Loading...")}</div></div><div class="panel"><h3>Bookings on my trips</h3><div id="driver-bookings">${loading("Loading...")}</div></div></div>`);
}

async function loadDriverDashboard() {
  try {
    const [trips, bookings] = await Promise.all([
      API.get("/api/trips/my"),
      API.get("/api/driver/bookings")
    ]);

    setText("stat-my-trips", trips.length);
    setText("stat-active-trips", trips.filter(t => t.status === "ACTIVE").length);
    setText("stat-driver-bookings", bookings.length);
    const revenue = bookings.filter(b => b.status === "CONFIRMED").reduce((s, b) => s + b.totalAmount, 0);
    setText("stat-driver-revenue", "₹" + revenue);

    document.getElementById("driver-trips").innerHTML = trips.length ? `<table class="table"><thead><tr><th>Route</th><th>Date</th><th>Seats</th><th>Fare</th><th>Status</th><th></th></tr></thead><tbody>${trips.map(t => `<tr><td><b>${esc(t.fromLocation)} → ${esc(t.toLocation)}</b></td><td>${esc(t.travelDate)}</td><td>${t.availableSeats}/${t.totalSeats}</td><td>₹${t.pricePerSeat}</td><td><span class="status ${t.status === "ACTIVE" ? "green" : t.status === "CANCELLED" ? "red" : "orange"}">${esc(t.status)}</span></td><td>${t.status !== "CANCELLED" ? `<button class="btn btn-danger btn-sm" onclick="cancelTrip(${t.tripId})">Cancel</button>` : ""}</td></tr>`).join("")}</tbody></table>` : emptyBlock("You haven't published any trips yet.");

    document.getElementById("driver-bookings").innerHTML = bookings.length ? bookings.map(b => `<div class="trip-card" style="grid-template-columns:1fr auto"><div><span class="status ${b.status === "CONFIRMED" ? "green" : "red"}">${esc(b.status)}</span><h3>${esc(b.travelerName)}</h3><p class="muted">${esc(b.fromLocation)} → ${esc(b.toLocation)} · ${esc(b.travelDate)}</p><p class="muted">${b.seatsBooked} seat(s) · ${money(b.totalAmount)}</p></div><div class="trip-actions">${b.status === "CONFIRMED" ? `<button class="btn btn-danger btn-sm" onclick="driverCancelBooking(${b.bookingId})">Cancel</button>` : ""}</div></div>`).join("") : emptyBlock("No bookings on your trips yet.");

  } catch (error) {
    console.error("Driver dashboard load error:", error);
    ["driver-trips", "driver-bookings"].forEach(id => { const el = document.getElementById(id); if (el) el.innerHTML = errorBlock(error.message || "Could not load dashboard data."); });
  }
}

// ---------- TRAVEL AGENCY ----------
function agencyDashShell(content) { return `${header()}<main class="dashboard-layout"><aside class="sidebar"><div class="side-title">Agency Workspace</div><a class="side-link active" href="#dashboard">⌂ Dashboard</a><a class="side-link" href="#agency-create-trip">＋ Create Departure</a><a class="side-link" href="#trips">Find Trips</a><div class="side-title">Account</div><a class="side-link" href="#profile">Profile</a><a class="side-link" href="#contact">Support</a><a class="side-link" href="#" onclick="logout()">Logout</a></aside><section class="dashboard-main"><div class="dash-head"><div><span class="eyebrow page-eyebrow">TRAVEL AGENCY</span><h1>Agency Dashboard</h1><p class="muted">Manage vehicles, departures, seats and traveler bookings from one workspace.</p></div><button class="btn btn-primary" onclick="go('agency-create-trip')">+ Create Departure</button></div>${content}</section></main>` }
function agencyDash(){return agencyDashShell(`
  <div class="stat-grid"><div class="stat">Active vehicles<div class="number" id="stat-ag-vehicles">–</div></div><div class="stat">Departures<div class="number" id="stat-ag-departures">–</div></div><div class="stat">Seats sold<div class="number" id="stat-ag-seats">–</div></div><div class="stat">Gross bookings<div class="number" id="stat-ag-revenue">–</div></div></div>
  <div class="dashboard-grid agency-dashboard-top"><div class="panel"><div class="panel-title-row"><div><h3>Vehicle Inventory</h3><p class="muted">Add each real vehicle once. Verified capacity controls the maximum seats you can publish.</p></div></div><form onsubmit="agencyAddVehicle(event)" class="form-grid agency-inline-form"><div class="field"><label>VEHICLE MODEL *</label><select id="agv-type" required><option value="">Loading vehicles…</option></select></div><div class="field"><label>REGISTRATION NUMBER *</label><input id="agv-reg" required placeholder="MH12AB1234"></div><div class="field"><label>ACTUAL CAPACITY *</label><select id="agv-cap" required disabled><option>Select vehicle first</option></select></div><div class="field" style="display:flex;align-items:end"><button class="btn btn-primary" id="agv-add-btn" style="width:100%">Add Vehicle</button></div></form><div id="agency-vehicles" class="agency-list">${loading('Loading vehicle inventory…')}</div></div><div class="panel"><div class="panel-title-row"><div><h3>Quick Publish</h3><p class="muted">Create a date-specific departure with seat-based or whole-vehicle selling.</p></div></div><div class="agency-quick-card"><div><strong>Seat-based</strong><span>Publish 1 to capacity seats and sell individually.</span></div><div><strong>Whole vehicle</strong><span>Publish the complete verified vehicle as one booking unit.</span></div><button class="btn btn-outline" onclick="go('agency-create-trip')">Create Departure →</button></div></div></div>
  <div class="panel" style="margin-top:18px"><div class="panel-title-row"><div><h3>Departures & Seat Inventory</h3><p class="muted">Every departure has its own published, booked and available seat count.</p></div><button class="btn btn-primary btn-sm" onclick="go('agency-create-trip')">+ New Departure</button></div><div class="table-wrap" id="agency-trips">${loading('Loading departures…')}</div></div>
  <div class="panel" style="margin-top:18px"><div class="panel-title-row"><div><h3>Traveler Bookings</h3><p class="muted">Cancel a confirmed booking when necessary; the seats are released immediately.</p></div></div><div id="agency-bookings">${loading('Loading bookings…')}</div></div>`)}

async function loadAgencyDashboard(){
  try{
    const [stats,vehicles,trips]=await Promise.all([API.get('/api/agency/stats'),API.get('/api/agency/vehicles'),API.get('/api/agency/trips')]);
    setText('stat-ag-vehicles',stats.vehicles);setText('stat-ag-departures',stats.departures);setText('stat-ag-seats',stats.seatsSold);setText('stat-ag-revenue',money(stats.grossRevenue||0));
    window.SPT_AGENCY_VEHICLES=vehicles;window.SPT_AGENCY_TRIPS=trips;
    const vType=document.getElementById('agv-type');
    if(vType){const current=vType.value;vType.innerHTML='<option value="">Select vehicle</option>'+VEHICLE_CATALOG.map(v=>`<option value="${esc(v.vehicleType)}">${esc(v.vehicleType)} · ${v.capacity} seats</option>`).join('');if(current)vType.value=current;updateAgencyVehicleCapacity();}
    const ve=document.getElementById('agency-vehicles');
    ve.innerHTML=vehicles.length?vehicles.map(v=>`<div class="agency-item"><div><strong>${esc(v.vehicleType)}</strong><span>${esc(v.registrationNumber)} · ${v.seatingCapacity} seats</span></div><span class="status ${v.status==='ACTIVE'?'green':'red'}">${esc(v.status)}</span>${v.status==='ACTIVE'?`<button class="btn btn-outline btn-sm" onclick="agencyDeactivateVehicle(${v.vehicleId})">Deactivate</button>`:''}</div>`).join(''):emptyBlock('No vehicles added yet. Add your first real vehicle above.');
    const te=document.getElementById('agency-trips');
    te.innerHTML=trips.length?`<table class="table"><thead><tr><th>Departure</th><th>Vehicle</th><th>Seats</th><th>Fare</th><th>Mode</th><th>Status</th><th></th></tr></thead><tbody>${trips.map(t=>`<tr><td><b>${esc(t.fromLocation)} → ${esc(t.toLocation)}</b><br><small>${esc(t.travelDate)} · ${esc(t.departureTime)}</small></td><td>${esc(t.vehicleType||'Vehicle')}</td><td>${t.availableSeats}/${t.totalSeats}</td><td>${money(t.pricePerSeat||0)} / seat</td><td>${t.bookingMode==='FULL_VEHICLE'?'Whole vehicle':'Seat based'}</td><td><span class="status ${t.status==='ACTIVE'?'green':t.status==='FULL'?'orange':'red'}">${esc(t.status)}</span></td><td>${t.status!=='CANCELLED'?`<button class="btn btn-outline btn-sm" onclick="agencyViewBookings(${t.tripId})">Bookings</button> <button class="btn btn-danger btn-sm" onclick="agencyCancelTrip(${t.tripId})">Cancel</button>`:''}</td></tr>`).join('')}</tbody></table>`:emptyBlock('No departures yet. Create your first departure.');
    const lists=await Promise.all(trips.map(t=>API.get(`/api/agency/trips/${t.tripId}/bookings`).catch(()=>[])));const all=lists.flat().map(b=>({...b,__trip:trips.find(t=>t.tripId===b.tripId)||trips.find(t=>t.tripId===b.tripId)}));
    const be=document.getElementById('agency-bookings');
    be.innerHTML=all.length?all.map(b=>{const t=trips.find(x=>x.tripId===b.tripId);return `<div class="agency-booking"><div><span class="status ${b.status==='CONFIRMED'?'green':b.status==='CANCELLED'?'red':'orange'}">${esc(b.status)}</span><strong>${esc(b.travelerName||('Traveler #'+b.travelerId))}</strong><span>${t?esc(t.fromLocation)+' → '+esc(t.toLocation):'Trip #'+b.tripId} · ${b.seatsBooked} seat(s) · ${money(b.totalAmount||0)}</span></div>${b.status==='CONFIRMED'?`<button class="btn btn-danger btn-sm" onclick="agencyCancelBooking(${b.bookingId})">Cancel & Release Seats</button>`:''}</div>`}).join(''):emptyBlock('No traveler bookings yet.');
  }catch(e){console.error('Agency dashboard',e);['agency-vehicles','agency-trips','agency-bookings'].forEach(id=>{const el=document.getElementById(id);if(el)el.innerHTML=errorBlock(e.message||'Could not load agency workspace.')});}
}
function updateAgencyVehicleCapacity(){const type=document.getElementById('agv-type')?.value;const v=VEHICLE_CATALOG.find(x=>x.vehicleType===type);const cap=document.getElementById('agv-cap');if(!cap)return;cap.disabled=!v;cap.innerHTML=v?`<option value="${v.capacity}">${v.capacity} seats</option>`:'<option>Select vehicle first</option>';}
async function agencyAddVehicle(e){e.preventDefault();const btn=document.getElementById('agv-add-btn');try{btn.disabled=true;btn.textContent='Adding…';await API.post('/api/agency/vehicles',{vehicleType:document.getElementById('agv-type').value,registrationNumber:document.getElementById('agv-reg').value.trim(),seatingCapacity:Number(document.getElementById('agv-cap').value)});toast('Vehicle added to inventory.');document.getElementById('agv-reg').value='';await loadAgencyDashboard();}catch(err){toast(err.message||'Could not add vehicle.');}finally{btn.disabled=false;btn.textContent='Add Vehicle';}}
async function agencyDeactivateVehicle(id){if(!confirm('Deactivate this vehicle? It will remain in your records but cannot be used for new departures.'))return;try{await API.delete(`/api/agency/vehicles/${id}`);toast('Vehicle deactivated.');loadAgencyDashboard();}catch(e){toast(e.message||'Could not deactivate vehicle.');}}
async function agencyViewBookings(id){const trips=window.SPT_AGENCY_TRIPS||[];const t=trips.find(x=>x.tripId===id);try{const rows=await API.get(`/api/agency/trips/${id}/bookings`);document.body.insertAdjacentHTML('beforeend',`<div class="modal-backdrop" id="agency-bookings-modal"><div class="modal agency-bookings-modal"><div class="modal-head"><div><span class="eyebrow page-eyebrow">PASSENGER MANIFEST</span><h2>${t?esc(t.fromLocation)+' → '+esc(t.toLocation):'Departure bookings'}</h2></div><button class="close" onclick="document.getElementById('agency-bookings-modal')?.remove()">×</button></div><div class="agency-manifest">${rows.length?rows.map(b=>`<div class="agency-manifest-row"><div><strong>Traveler #${b.travelerId}</strong><span>${b.seatsBooked} seat(s) · ${money(b.totalAmount||0)}</span></div><span class="status ${b.status==='CONFIRMED'?'green':b.status==='CANCELLED'?'red':'orange'}">${esc(b.status)}</span></div>`).join(''):emptyBlock('No bookings for this departure.')}</div></div></div>`);}catch(e){toast(e.message||'Could not load bookings.');}}
async function agencyCancelBooking(id){if(!confirm('Cancel this confirmed booking and release its seats immediately?'))return;try{await API.delete(`/api/agency/bookings/${id}`);toast('Booking cancelled and seats released.');loadAgencyDashboard();}catch(e){toast(e.message||'Could not cancel booking.');}}
async function agencyCancelTrip(id){if(!confirm('Cancel this departure? Its confirmed traveler bookings will be cancelled.'))return;try{await API.delete(`/api/agency/trips/${id}`);toast('Departure cancelled.');loadAgencyDashboard();}catch(e){toast(e.message||'Could not cancel departure.');}}

function agencyCreateTrip(){
  return `${header()}<main class="page"><div class="page-header"><div class="container"><span class="eyebrow page-eyebrow">AGENCY INVENTORY</span><h1>Create a Departure</h1><p class="section-sub">Choose a real agency vehicle, publish only the seats you want to sell, and set the departure-specific route and fare.</p></div></div><section class="section"><div class="container"><form onsubmit="agencyCreateTripSubmit(event)" class="dashboard-grid agency-create-grid"><div class="panel"><h3>Departure details</h3><div class="form-grid"><div class="field"><label>PICKUP / SOURCE *</label><input id="agt-source" required placeholder="Pune"></div><div class="field"><label>DESTINATION *</label><input id="agt-dest" required placeholder="Rajgad Fort"></div><div class="field"><label>DATE *</label><input id="agt-date" type="date" required></div><div class="field"><label>DEPARTURE TIME *</label><input id="agt-time" type="time" value="06:00" required></div><div class="field full"><label>PICKUP POINTS</label><textarea id="agt-pickups" rows="3" placeholder="Shivajinagar · Wakad · Hinjewadi"></textarea><small class="muted">Optional pickup points shown to travelers.</small></div><div class="field full"><label>ONE-WAY ROUTE DISTANCE (KM) *</label><input id="agt-distance" type="number" min="1" step="0.1" required placeholder="Confirmed route distance"><small class="muted">Used for server-side fare calculation. Customers see only the final fare.</small></div></div></div><div class="panel"><h3>Vehicle & selling mode</h3><div class="form-grid"><div class="field full"><label>AGENCY VEHICLE *</label><select id="agt-vehicle" required onchange="agencyVehicleChanged()"><option value="">Loading inventory…</option></select></div><div class="field"><label>VERIFIED CAPACITY</label><input id="agt-capacity" readonly value="PENDING"></div><div class="field"><label>PUBLISHED SEATS *</label><input id="agt-seats" type="number" min="1" required value="1" oninput="agencyPreviewFare()"></div><div class="field"><label>SELLING MODE *</label><select id="agt-mode" onchange="agencyModeChanged()"><option value="SEAT_BASED">Seat based — travelers book seats</option><option value="FULL_VEHICLE">Whole vehicle — one booking blocks all seats</option></select></div><div class="field"><label>TRIP TYPE</label><select id="agt-return" onchange="agencyPreviewFare()"><option value="false">One way</option><option value="true">Return</option></select></div></div><div id="agt-fare-preview" class="fare-preview"><strong>Fare preview</strong><span>Select vehicle, seats and route distance.</span></div><button class="btn btn-primary" id="agt-submit" style="width:100%;margin-top:18px">Publish Departure</button><p class="form-hint">Seat inventory is stored per departure. Booking uses a database row lock, so the same last seat cannot be sold twice.</p></div></form></div></section></main>`;
}
async function hydrateAgencyCreateTrip(){try{const dateEl=document.getElementById('agt-date');if(dateEl){const today=new Date();const yyyy=today.getFullYear();const mm=String(today.getMonth()+1).padStart(2,'0');const dd=String(today.getDate()).padStart(2,'0');const todayIso=`${yyyy}-${mm}-${dd}`;dateEl.min=todayIso;if(dateEl.value && dateEl.value<todayIso)dateEl.value='';}await loadVehicleCatalog();const el=document.getElementById('agt-vehicle');const vs=window.SPT_AGENCY_VEHICLES||await API.get('/api/agency/vehicles');window.SPT_AGENCY_VEHICLES=vs;const active=vs.filter(v=>v.status==='ACTIVE');el.innerHTML='<option value="">Select vehicle</option>'+active.map(v=>`<option value="${v.vehicleId}">${esc(v.vehicleType)} · ${esc(v.registrationNumber)} · ${v.seatingCapacity} seats</option>`).join('');if(!active.length)toast('Add an active vehicle in your Agency Dashboard before creating a departure.');else{el.value=active[0].vehicleId;agencyVehicleChanged();}}catch(e){toast(e.message||'Could not load agency vehicles.');}}
function agencyVehicleChanged(){const id=Number(document.getElementById('agt-vehicle')?.value);const v=(window.SPT_AGENCY_VEHICLES||[]).find(x=>x.vehicleId===id);const cap=document.getElementById('agt-capacity'),seats=document.getElementById('agt-seats');if(!v){if(cap)cap.value='PENDING';return;}if(cap)cap.value=v.seatingCapacity+' seats';if(seats){seats.max=v.seatingCapacity;seats.value=Math.min(Number(seats.value||1),v.seatingCapacity);}agencyModeChanged();agencyPreviewFare();}
function agencyModeChanged(){const id=Number(document.getElementById('agt-vehicle')?.value);const v=(window.SPT_AGENCY_VEHICLES||[]).find(x=>x.vehicleId===id);const mode=document.getElementById('agt-mode')?.value;const seats=document.getElementById('agt-seats');if(!seats||!v)return;if(mode==='FULL_VEHICLE'){seats.value=v.seatingCapacity;seats.readOnly=true;seats.max=v.seatingCapacity;}else{seats.readOnly=false;seats.max=v.seatingCapacity;seats.value=Math.min(Number(seats.value||1),v.seatingCapacity);}agencyPreviewFare();}
async function agencyPreviewFare(){const box=document.getElementById('agt-fare-preview');const id=Number(document.getElementById('agt-vehicle')?.value);const v=(window.SPT_AGENCY_VEHICLES||[]).find(x=>x.vehicleId===id);const km=Number(document.getElementById('agt-distance')?.value||0);const seats=Number(document.getElementById('agt-seats')?.value||0);const ret=document.getElementById('agt-return')?.value==='true';if(!box||!v||km<=0||seats<=0)return;try{const q=await API.get(`/api/trips/pricing/quote${qs({vehicleType:v.vehicleType,seats,distanceKm:km,returnTrip:ret})}`);box.innerHTML=`<strong>Estimated customer fare</strong><b>${money(q.pricePerPerson||0)} / seat</b><span>${esc(v.vehicleType)} · ${seats} published seat${seats>1?'s':''} · ${ret?'Return':'One way'}</span>`;}catch(e){box.innerHTML=emptyBlock(e.message||'Fare preview unavailable.');}}
async function agencyCreateTripSubmit(e){e.preventDefault();const btn=document.getElementById('agt-submit');const dateEl=document.getElementById('agt-date');const travelDate=dateEl?.value||'';const today=new Date();const todayIso=`${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;if(travelDate<todayIso){toast('Departure date cannot be in the past. Please choose today or a future date.');dateEl?.focus();return;}const id=Number(document.getElementById('agt-vehicle').value);const v=(window.SPT_AGENCY_VEHICLES||[]).find(x=>x.vehicleId===id);if(!v){toast('Please select an active agency vehicle.');return;}const seats=Number(document.getElementById('agt-seats').value||0);if(seats<1||seats>Number(v.seatingCapacity)){toast(`Published seats must be between 1 and ${v.seatingCapacity}.`);return;}const distance=Number(document.getElementById('agt-distance').value||0);if(distance<=0){toast('Please enter a valid one-way route distance.');document.getElementById('agt-distance')?.focus();return;}const body={fromLocation:document.getElementById('agt-source').value.trim(),toLocation:document.getElementById('agt-dest').value.trim(),travelDate,departureTime:document.getElementById('agt-time').value,agencyVehicleId:id,publishedSeats:seats,distanceKm:distance,returnTrip:document.getElementById('agt-return').value==='true',bookingMode:document.getElementById('agt-mode').value,pickupPoints:document.getElementById('agt-pickups').value.trim()};try{btn.disabled=true;btn.textContent='Publishing…';await API.post('/api/agency/trips',body);toast('Departure published successfully.');go('dashboard');}catch(err){console.error('Agency departure publish error:',err);toast(err.message||'Could not publish departure.');}finally{btn.disabled=false;btn.textContent='Publish Departure';}}

// ---------- HOTEL OWNER ----------
function hotelDash() {
  return dashShell("Stay Owner Dashboard", "Manage your live properties and guest bookings.", `
  <div class="stat-grid"><div class="stat">My properties<div class="number" id="stat-my-props">–</div></div><div class="stat">Active<div class="number" id="stat-active-props">–</div></div><div class="stat">Total bookings<div class="number" id="stat-hotel-bookings">–</div></div><div class="stat">Revenue<div class="number" id="stat-hotel-revenue">–</div></div></div>
  <div class="dashboard-grid"><div class="panel"><div class="dash-head"><h3>My properties</h3><button class="btn btn-primary btn-sm" onclick="go('create-property')">+ List Property</button></div><div class="table-wrap" id="hotel-properties">${loading("Loading...")}</div></div><div class="panel"><h3>Bookings</h3><div id="hotel-bookings">${loading("Loading...")}</div></div></div>`);
}

async function loadHotelDashboard() {
  try {
    const properties = await API.get("/api/properties/my");
    setText("stat-my-props", properties.length);
    setText("stat-active-props", properties.filter(p => p.status === "ACTIVE").length);

    document.getElementById("hotel-properties").innerHTML = properties.length ? `<table class="table"><thead><tr><th>Name</th><th>Location</th><th>Rooms</th><th>Price/night</th><th>Status</th><th></th></tr></thead><tbody>${properties.map(p => `<tr><td><b>${esc(p.propertyName)}</b></td><td>${esc(p.location)}</td><td>${p.availableRooms}/${p.totalRooms}</td><td>${money(p.pricePerNight)}</td><td><span class="status ${p.status === "ACTIVE" ? "green" : "red"}">${esc(p.status)}</span></td><td>${p.status === "ACTIVE" ? `<button class="btn btn-danger btn-sm" onclick="cancelProperty(${p.propertyId})">Deactivate</button>` : ""}</td></tr>`).join("")}</tbody></table>` : emptyBlock("You haven't listed any properties yet.");

    const bookingLists = await Promise.all(properties.map(p => API.get(`/api/property-bookings/property/${p.propertyId}`).catch(() => [])));
    const allBookings = bookingLists.flat();

    setText("stat-hotel-bookings", allBookings.length);
    const revenue = allBookings.filter(b => b.status === "CONFIRMED").reduce((s, b) => s + b.totalAmount, 0);
    setText("stat-hotel-revenue", "₹" + revenue);

    document.getElementById("hotel-bookings").innerHTML = allBookings.length ? allBookings.map(b => `<div class="trip-card" style="grid-template-columns:1fr auto"><div><span class="status ${b.status === "CONFIRMED" ? "green" : "red"}">${esc(b.status)}</span><h3>${esc(b.propertyName)}</h3><p class="muted">${esc(b.checkInDate)} → ${esc(b.checkOutDate)}</p><p class="muted">${b.roomsBooked} room(s) · ${money(b.totalAmount)} · ${esc(b.paymentStatus||b.paymentMethod||'PAY AT HOTEL')}</p><p class="muted">${esc(b.checkInStatus||'NOT_CHECKED_IN')} · ${esc(b.checkOutStatus||'NOT_CHECKED_OUT')}</p></div><div class="trip-actions">${b.checkInStatus!=='CHECKED_IN'?`<button class="btn btn-primary btn-sm" onclick="hotelCheckIn(${b.bookingId})">Check-in</button>`:''}${b.checkInStatus==='CHECKED_IN'&&b.checkOutStatus!=='CHECKED_OUT'?`<button class="btn btn-outline btn-sm" onclick="hotelCheckOut(${b.bookingId})">Check-out</button>`:''}</div></div>`).join("") : emptyBlock("No bookings yet.");

  } catch (error) {
    console.error("Hotel dashboard load error:", error);
    ["hotel-properties", "hotel-bookings"].forEach(id => { const el = document.getElementById(id); if (el) el.innerHTML = errorBlock(error.message || "Could not load dashboard data."); });
  }
}

// ---------- ADMIN ----------
function adminDash() {
  return dashShell("Admin Control Center", "Live platform statistics from the database.", `
  <div class="stat-grid"><div class="stat">Users<div class="number" id="a-users">–</div></div><div class="stat">Trips<div class="number" id="a-trips">–</div></div><div class="stat">Trip bookings<div class="number" id="a-bookings">–</div></div><div class="stat">Properties<div class="number" id="a-properties">–</div></div></div>
  <div class="dashboard-grid"><div class="panel"><h3>User breakdown</h3><div id="a-user-breakdown">${loading("Loading...")}</div></div><div class="panel"><h3>Booking breakdown</h3><div id="a-booking-breakdown">${loading("Loading...")}</div></div></div><div class="dashboard-grid" style="margin-top:18px"><div class="panel"><h3>Driver verification</h3><div id="a-drivers">${loading("Loading...")}</div></div><div class="panel"><h3>Agency verification</h3><div id="a-agencies">${loading("Loading...")}</div></div></div><div class="dashboard-grid" style="margin-top:18px"><div class="panel"><h3>Complaints</h3><div id="a-complaints">${loading("Loading...")}</div></div><div class="panel"><h3>SOS alerts</h3><div id="a-sos">${loading("Loading...")}</div></div></div>
  <div class="panel" style="margin-top:18px"><div class="dash-head"><div><h3>Vehicle Rate Card</h3><p class="muted">Internal pricing reference. Customers never see per-km partner rates.</p></div></div><div id="a-rate-card">${loading("Loading rate card...")}</div></div>
  <div class="panel" style="margin-top:18px"><div class="dash-head"><div><h3>Hotel / Stay Owner applications</h3><p class="muted">Review new stay-partner registrations before account activation.</p></div></div><div id="a-hotels">${loading("Loading...")}</div></div>`);
}

async function loadAdminDashboard() {
  try {
    const s = await API.get("/api/admin/stats");
    setText("a-users", s.totalUsers);
    setText("a-trips", s.totalTrips);
    setText("a-bookings", s.totalBookings);
    setText("a-properties", s.totalProperties);

    document.getElementById("a-user-breakdown").innerHTML = `
      <p>Travelers: <b>${s.totalTravelers}</b></p>
      <p>Drivers: <b>${s.totalDrivers}</b></p>
      <p>Hotel owners: <b>${s.totalHotelOwners}</b></p>
      <hr style="border-color:var(--line);margin:14px 0">
      <p>Active trips: <b>${s.activeTrips}</b> · Cancelled: <b>${s.cancelledTrips}</b></p>
      <p>Active properties: <b>${s.activeProperties}</b> · Inactive: <b>${s.inactiveProperties}</b></p>`;

    document.getElementById("a-booking-breakdown").innerHTML = `
      <p>Trip bookings — Confirmed: <b>${s.confirmedBookings}</b> · Cancelled: <b>${s.cancelledBookings}</b></p>
      <p>Property bookings — Total: <b>${s.totalPropertyBookings}</b>, Confirmed: <b>${s.confirmedPropertyBookings}</b>, Cancelled: <b>${s.cancelledPropertyBookings}</b></p>`;

    const [drivers, agencies, complaints, sos, hotels, rateCard] = await Promise.all([API.get("/api/admin/drivers"), API.get("/api/admin/agencies"), API.get("/api/admin/complaints"), API.get("/api/admin/sos"), API.get("/api/admin/hotel-applications"), API.get("/api/trips/pricing/rates")]);
    const rateEl = document.getElementById("a-rate-card");
    if (rateEl) rateEl.innerHTML = `<div class="table-wrap"><table class="table"><thead><tr><th>Vehicle</th><th>Category</th><th>Capacity</th><th>Internal rate/km</th></tr></thead><tbody>${rateCard.vehicles.map(v => `<tr><td><b>${esc(v.vehicleType)}</b></td><td>${esc(v.category)}</td><td>${v.capacity}</td><td>₹${v.ratePerKm}</td></tr>`).join("")}</tbody></table></div><p class="muted" style="margin-top:10px">Platform margin: ${rateCard.platformMarginPercent}% · Service fee: ₹${rateCard.serviceFee} / booking</p>`;
    document.getElementById("a-drivers").innerHTML = drivers.length ? drivers.map(d => `<div style="border-bottom:1px solid var(--line);padding:10px 0"><b>${esc(d.fullName)}</b> · ${esc(d.vehicleType)} · ${d.seatingCapacity} seats<br><span class="status ${d.verificationStatus==='APPROVED'?'green':d.verificationStatus==='REJECTED'?'red':'orange'}">${d.verificationStatus}</span> <button class="btn btn-outline btn-sm" onclick='showDriverDocs(${JSON.stringify(d)})'>View Documents</button> ${d.verificationStatus!=='APPROVED'?`<button class="btn btn-primary btn-sm" onclick="reviewDriver(${d.driverProfileId},'APPROVED')">Approve</button>`:''} ${d.verificationStatus!=='REJECTED'?`<button class="btn btn-danger btn-sm" onclick="reviewDriver(${d.driverProfileId},'REJECTED')">Reject</button>`:''}</div>`).join("") : emptyBlock("No driver profiles.");
    document.getElementById("a-agencies").innerHTML = agencies.length ? agencies.map(a => `<div style="border-bottom:1px solid var(--line);padding:10px 0"><b>${esc(a.businessName)}</b> · ${esc(a.cityDistrict)}<br><span class="status ${a.verificationStatus==='APPROVED'?'green':a.verificationStatus==='REJECTED'?'red':'orange'}">${a.verificationStatus}</span> <button class="btn btn-outline btn-sm" onclick='showAgencyDocs(${JSON.stringify(a)})'>View Documents</button> ${a.verificationStatus!=='APPROVED'?`<button class="btn btn-primary btn-sm" onclick="reviewAgency(${a.agencyId},'APPROVED')">Approve</button>`:''} ${a.verificationStatus!=='REJECTED'?`<button class="btn btn-danger btn-sm" onclick="reviewAgency(${a.agencyId},'REJECTED')">Reject</button>`:''}</div>`).join("") : emptyBlock("No agency profiles.");
    document.getElementById("a-complaints").innerHTML = complaints.length ? complaints.slice(0,8).map(c => `<div style="padding:8px 0;border-bottom:1px solid var(--line)"><b>#${c.complaintId} ${esc(c.subject)}</b><br><span class="status orange">${c.status}</span> · ${esc(c.category)}</div>`).join("") : emptyBlock("No complaints.");
    document.getElementById("a-sos").innerHTML = sos.length ? sos.slice(0,8).map(x => `<div style="padding:8px 0;border-bottom:1px solid var(--line)"><b>#${x.sosId}</b> · ${esc(x.status)}<br>${x.latitude&&x.longitude?` ${x.latitude}, ${x.longitude}`:'Location unavailable'}</div>`).join("") : emptyBlock("No SOS alerts.");
     const hotelEl = document.getElementById("a-hotels");
     if (hotelEl) hotelEl.innerHTML = hotels.length ? hotels.slice(0,10).map(a => `<div class="admin-application-card"><div><b>${esc(a.propertyName)}</b><span class="muted"> · ${esc(a.ownerName)} · ${esc(a.cityDistrict)}</span><br><small>${esc(a.email)} · ${esc(a.phone)} · ${esc(a.propertyType)} · ${a.totalRooms} rooms · ${money(a.pricePerNight)}/night</small><p class="muted">${esc(a.description)}</p><span class="status ${a.status==='APPROVED'?'green':a.status==='REJECTED'?'red':'orange'}">${esc(a.status)}</span></div>${a.status==='PENDING'?`<div class="admin-application-actions"><button class="btn btn-primary btn-sm" onclick="reviewHotelApplication(${a.applicationId},'APPROVED')">Approve</button><button class="btn btn-danger btn-sm" onclick="reviewHotelApplication(${a.applicationId},'REJECTED')">Reject</button></div>`:''}</div>`).join("") : emptyBlock("No hotel owner applications.");

  } catch (error) {
    console.error("Admin dashboard load error:", error);
    ["a-user-breakdown", "a-booking-breakdown"].forEach(id => { const el = document.getElementById(id); if (el) el.innerHTML = errorBlock(error.message || "Could not load admin stats."); });
  }
}

function setText(id, value) { const el = document.getElementById(id); if (el) el.textContent = value; }

function openDocument(path){if(!path)return;const token=localStorage.getItem('spt_token');fetch(`${window.SPT_API_BASE_URL}/api/documents?path=${encodeURIComponent(path)}`,{headers:{Authorization:`Bearer ${token}`}}).then(async r=>{if(!r.ok)throw new Error('Unable to open document');const blob=await r.blob();const url=URL.createObjectURL(blob);window.open(url,'_blank');setTimeout(()=>URL.revokeObjectURL(url),60000)}).catch(e=>toast(e.message))}
function showDriverDocs(d){const fields=[['Aadhaar',d.aadhaarDocument],['Driving Licence',d.drivingLicenceDocument],['PAN',d.panDocument],['Address Proof',d.addressProofDocument],['RC',d.rcDocument],['Insurance',d.insuranceDocument],['PUC',d.pucDocument],['Commercial Permit',d.commercialPermitDocument],['Fitness Certificate',d.fitnessCertificateDocument],['Vehicle Photo',d.vehiclePhoto]];document.body.insertAdjacentHTML('beforeend',`<div class="modal-backdrop" id="doc-modal"><div class="modal"><div class="modal-head"><h2>Driver Documents</h2><button class="close" onclick="document.getElementById('doc-modal')?.remove()">×</button></div>${fields.map(x=>x[1]?`<div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;border-bottom:1px solid var(--line)"><span>${x[0]}</span><button class="btn btn-outline btn-sm" onclick="openDocument('${esc(x[1])}')">View</button></div>`:'').join('')}</div></div>`)}
function showAgencyDocs(a){const fields=[['Aadhaar',a.aadhaarDocument],['PAN/Udyam',a.panUdyamDocument],['Bank Passbook',a.bankPassbookDocument],['Owner ID',a.ownerIdDocument],['Shop Act',a.shopActDocument],['GST',a.gstCertificate],['MTDC/Tourism',a.mtdcTourismCertificate],['Partnership/Incorporation',a.partnershipOrIncorporationCertificate],['Commercial Vehicle Docs',a.commercialVehicleDocuments],['IATA/IRCTC',a.iataIrctcLicense],['Agency Logo',a.agencyLogo]];document.body.insertAdjacentHTML('beforeend',`<div class="modal-backdrop" id="doc-modal"><div class="modal"><div class="modal-head"><h2>Agency Documents</h2><button class="close" onclick="document.getElementById('doc-modal')?.remove()">×</button></div>${fields.map(x=>x[1]?`<div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;border-bottom:1px solid var(--line)"><span>${x[0]}</span><button class="btn btn-outline btn-sm" onclick="openDocument('${esc(x[1])}')">View</button></div>`:'').join('')}</div></div>`)}

async function reviewDriver(id,status){try{await API.put(`/api/admin/drivers/${id}/verification`,{status});toast(`Driver ${status.toLowerCase()}.`);loadAdminDashboard()}catch(e){toast(e.message)}}
async function reviewAgency(id,status){try{await API.put(`/api/admin/agencies/${id}/verification`,{status});toast(`Agency ${status.toLowerCase()}.`);loadAdminDashboard()}catch(e){toast(e.message)}}

async function reviewHotelApplication(id,status){
  const action = status === "APPROVED" ? "approve" : "reject";
  if(!confirm(`Are you sure you want to ${action} this Hotel / Stay Owner application?`)) return;
  try{
    const result = await API.put(`/api/admin/hotel-applications/${id}/review`,{status});
    toast(result?.message || `Hotel application ${action}d.`);
    await loadAdminDashboard();
  }catch(e){
    console.error("Hotel application review error:", e);
    toast(e.message || "Could not update hotel application.");
  }
}

// =====================================================
// PROFILE — GET/PUT /api/users/profile, PUT /api/users/password
// =====================================================
function profile() {
  return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Profile & Account</h1><p class="section-sub">Manage your account and emergency contact details securely.</p></div></div><section class="section"><div class="container profile-grid">
  <div class="panel profile-summary" id="profile-info">${loading("Loading profile...")}</div>

  <div class="panel">
    <div class="panel-title-row"><div><h3>Personal details</h3><p class="muted">Keep your phone number up to date.</p></div></div>
    <form onsubmit="updateProfileSubmit(event)">
      <div class="field"><label>FULL NAME</label><input id="pf-name" required autocomplete="name"></div>
      <div class="field" style="margin-top:12px"><label>PHONE</label><input id="pf-phone" inputmode="numeric" maxlength="10" autocomplete="tel"></div>
      <button class="btn btn-primary" style="width:100%;margin-top:14px" id="pf-submit-btn">Save personal details</button>
    </form>
  </div>

  <div class="panel emergency-panel">
    <div class="panel-title-row">
      <div><h3>Emergency contact</h3><p class="muted">Used automatically when you trigger SOS.</p></div>
      <span class="contact-badge">Safety</span>
    </div>
    <form onsubmit="updateProfileSubmit(event)">
      <div class="field"><label>CONTACT NAME</label><input id="pf-emergency-name" placeholder="e.g. Shweta" autocomplete="name"></div>
      <div class="field" style="margin-top:12px"><label>CONTACT NUMBER</label><input id="pf-emergency-number" inputmode="numeric" maxlength="10" pattern="[6-9][0-9]{9}" placeholder="10-digit mobile number" autocomplete="tel"></div>
      <div class="field" style="margin-top:12px"><label>RELATION</label><select id="pf-emergency-relation">
        <option value="">Select relation</option>
        <option>Mother</option><option>Father</option><option>Brother</option><option>Sister</option><option>Spouse</option><option>Friend</option><option>Relative</option><option>Other</option>
      </select></div>
      <p class="form-hint">This contact is not taken from the SOS form. It is saved to your profile and reused automatically.</p>
      <div class="profile-actions">
        ${state.user?.role === "DRIVER" ? "" : '<button class="btn btn-danger-soft" type="button" onclick="clearEmergencyContact()">Remove contact</button>'}
        <button class="btn btn-primary" type="submit" id="pf-emergency-submit">Save emergency contact</button>
      </div>
    </form>
  </div>

  </div></section></main>${footer()}` }

async function loadProfile() {
  const el = document.getElementById("profile-info");
  if (!el) return;
  try {
    const user = await API.get("/api/users/profile");
    let emergency = user;
    if (state.user?.role === "DRIVER") {
      emergency = await API.get("/api/driver/emergency-contact");
    }
    el.innerHTML = `<div class="profile-avatar">${esc((user.name || "U").charAt(0).toUpperCase())}</div><h3>${esc(user.name)}</h3><p class="muted">${esc(user.email)}</p><p class="muted">Phone: ${esc(user.phone || "No phone on file")}</p><span class="status green">${esc(user.role)}</span>
      <div class="emergency-summary"><span>Emergency contact</span><b>${emergency.emergencyContactName ? esc(emergency.emergencyContactName) : "Not added"}</b><small>${emergency.emergencyContactNumber ? esc(emergency.emergencyContactNumber) : "Add a number before using SOS"}</small></div>`;
    const nameInput = document.getElementById("pf-name");
    const phoneInput = document.getElementById("pf-phone");
    const emergencyName = document.getElementById("pf-emergency-name");
    const emergencyNumber = document.getElementById("pf-emergency-number");
    const emergencyRelation = document.getElementById("pf-emergency-relation");
    if (nameInput) nameInput.value = user.name || "";
    if (phoneInput) phoneInput.value = user.phone || "";
    if (emergencyName) emergencyName.value = emergency.emergencyContactName || "";
    if (emergencyNumber) emergencyNumber.value = emergency.emergencyContactNumber || "";
    if (emergencyRelation) emergencyRelation.value = emergency.emergencyContactRelation || "";
  } catch (error) {
    el.innerHTML = errorBlock(error.message || "Could not load profile.");
  }
}

async function updateProfileSubmit(e) {
  e.preventDefault();
  const btn = e.submitter || document.getElementById("pf-submit-btn");
  const isEmergencyForm = btn?.id === "pf-emergency-submit";
  try {
    if (btn) { btn.disabled = true; btn.textContent = "Saving..."; }

    if (isEmergencyForm && state.user?.role === "DRIVER") {
      await API.put("/api/driver/emergency-contact", {
        emergencyContactName: document.getElementById("pf-emergency-name").value.trim(),
        emergencyContactNumber: document.getElementById("pf-emergency-number").value.trim(),
        emergencyContactRelation: document.getElementById("pf-emergency-relation").value
      });
      toast("Emergency contact saved.");
    } else {
      const body = {
        name: document.getElementById("pf-name").value.trim(),
        phone: document.getElementById("pf-phone").value.trim(),
        emergencyContactName: document.getElementById("pf-emergency-name").value.trim(),
        emergencyContactNumber: document.getElementById("pf-emergency-number").value.trim(),
        emergencyContactRelation: document.getElementById("pf-emergency-relation").value
      };
      const updated = await API.put("/api/users/profile", body);
      const user = { ...state.user, name: updated.name };
      localStorage.setItem("spt_user", JSON.stringify(user));
      state.user = user;
      toast(isEmergencyForm ? "Emergency contact saved." : "Profile and emergency contact saved.");
    }
    await loadProfile();
  } catch (error) {
    toast(error.message || "Could not update profile.");
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = btn.id === "pf-emergency-submit" ? "Save emergency contact" : "Save personal details"; }
  }
}

async function clearEmergencyContact() {
  const name = document.getElementById("pf-name")?.value.trim();
  const phone = document.getElementById("pf-phone")?.value.trim();
  if (!name) { toast("Please wait for your profile to load."); return; }
  try {
    const btn = document.getElementById("pf-emergency-submit");
    if (btn) btn.disabled = true;
    if (state.user?.role === "DRIVER") {
      throw new Error("Driver emergency contact cannot be removed after verification; update it to a valid contact instead.");
    }
    await API.put("/api/users/profile", {
      name,
      phone,
      emergencyContactName: "",
      emergencyContactNumber: "",
      emergencyContactRelation: ""
    });
    toast("Emergency contact removed.");
    await loadProfile();
  } catch (error) {
    toast(error.message || "Could not remove emergency contact.");
  } finally {
    const btn = document.getElementById("pf-emergency-submit");
    if (btn) btn.disabled = false;
  }
}

async function changePasswordSubmit(e) {
  e.preventDefault();
  const btn = document.getElementById("pf-pass-btn");
  const currentPassword = document.getElementById("pf-current-pass").value;
  const newPassword = document.getElementById("pf-new-pass").value;
  const confirmPassword = document.getElementById("pf-confirm-pass")?.value;
  if (confirmPassword !== undefined && newPassword !== confirmPassword) {
    toast("New passwords do not match.");
    return;
  }
  const body = { currentPassword, newPassword };
  try {
    if (btn) { btn.disabled = true; btn.textContent = "Updating..."; }
    await API.put("/api/users/password", body);
    toast("Password changed successfully.");
    e.target.reset();
    setTimeout(() => go("profile"), 600);
  } catch (error) {
    toast(error.message || "Could not change password.");
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = "Update Password"; }
  }
}

// =====================================================
// SETTINGS — CHANGE PASSWORD
// =====================================================
function settingsPassword() {
  if (!state.user) return loginPrompt("Login to manage your account settings.");
  return `${header()}<main class="page"><div class="page-header"><div class="container"><span class="eyebrow page-eyebrow">ACCOUNT SECURITY</span><h1>Change Password</h1><p class="section-sub">Update your password securely from account settings.</p></div></div>
  <section class="section"><div class="container"><div class="panel settings-password-card">
    <div class="settings-security-icon">✓</div>
    <h2>Keep your account secure</h2>
    <p class="muted">Choose a strong password that you do not reuse on another website.</p>
    <form onsubmit="changePasswordSubmit(event)" class="settings-password-form">
      <div class="field"><label>CURRENT PASSWORD</label><input id="pf-current-pass" type="password" required autocomplete="current-password"></div>
      <div class="field"><label>NEW PASSWORD</label><input id="pf-new-pass" type="password" required minlength="8" autocomplete="new-password" placeholder="Minimum 8 characters"></div>
      <div class="field"><label>CONFIRM NEW PASSWORD</label><input id="pf-confirm-pass" type="password" required minlength="8" autocomplete="new-password" placeholder="Re-enter new password"></div>
      <button class="btn btn-primary" id="pf-pass-btn">Update Password</button>
    </form>
  </div></div></section></main>${footer()}`;
}

// =====================================================
// PARTNER REGISTRATION / SAFETY / COMPLAINTS
// =====================================================
function agencyRegister() {
  return `${header()}<main class="auth-wrap"><div class="auth-card" style="max-width:900px"><h1>Travel Agency Registration</h1><p class="muted">Mandatory documents are verified by Admin before the agency becomes active.</p><form onsubmit="agencyRegisterSubmit(event)">
  <div class="form-grid"><div class="field"><label>BUSINESS / AGENCY NAME *</label><input id="ag-business" required></div><div class="field"><label>OWNER NAME *</label><input id="ag-owner" required></div><div class="field"><label>MOBILE *</label><input id="ag-mobile" inputmode="numeric" pattern="[6-9][0-9]{9}" maxlength="10" required></div><div class="field"><label>EMAIL *</label><input id="ag-email" type="email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}" required></div><div class="field"><label>PASSWORD *</label><input id="ag-pass" type="password" minlength="8" required></div><div class="field"><label>AGENCY TYPE</label><select id="ag-type"><option>Travel Agency</option><option>Tour Operator</option><option>Adventure / Trek Operator</option><option>Transport Operator</option><option>Other</option></select></div><div class="field full"><label>BUSINESS ADDRESS *</label><textarea id="ag-address" rows="3" required></textarea></div><div class="field"><label>CITY / DISTRICT *</label><input id="ag-city" required></div><div class="field"><label>NUMBER OF VEHICLES (OPTIONAL)</label><input id="ag-vehicles" type="number" min="0"></div><div class="field"><label>NUMBER OF DRIVERS (OPTIONAL)</label><input id="ag-drivers" type="number" min="0"></div></div>
  <h3 style="margin-top:24px">Mandatory Documents</h3><div class="form-grid">${fileField('ag-aadhaar','Aadhaar Card *',true)}${fileField('ag-pan','PAN / Udyam *',true)}${fileField('ag-bank','Bank Passbook *',true)}${fileField('ag-ownerid','Owner ID Proof (Aadhaar / PAN) *',true)}${fileField('ag-shop','Shop Act License *',true)}</div>
  <h3 style="margin-top:24px">Optional / If Applicable</h3><div class="form-grid">${fileField('ag-gst','GST Certificate')}${fileField('ag-mtdc','MTDC / Ministry of Tourism Certificate')}${fileField('ag-inc','Partnership Deed / Company Incorporation Certificate')}${fileField('ag-commercial','Commercial Vehicle Documents (RC, Insurance, Tourist Permit)')}${fileField('ag-iata','IATA / IRCTC License Copy')}${fileField('ag-logo','Agency Logo / Photo')}</div>
  <label class="check" style="margin:18px 0"><input type="checkbox" required> I confirm these documents belong to this agency/business.</label><button class="btn btn-primary" style="width:100%" id="ag-submit">Submit for Verification</button></form></div></main>${footer()}`;
}
function fileField(id,label,required=false){return `<div class="field"><label>${label}</label><input id="${id}" type="file" ${required?'required':''} accept="image/*,.pdf"></div>`}
async function agencyRegisterSubmit(e){e.preventDefault();const fd=new FormData();const fields={businessName:'ag-business',ownerName:'ag-owner',mobile:'ag-mobile',email:'ag-email',password:'ag-pass',agencyType:'ag-type',businessAddress:'ag-address',cityDistrict:'ag-city',numberOfVehicles:'ag-vehicles',numberOfDrivers:'ag-drivers'};Object.entries(fields).forEach(([k,id])=>{const v=document.getElementById(id)?.value;if(v)fd.append(k,v)});[['aadhaarDocument','ag-aadhaar'],['panUdyamDocument','ag-pan'],['bankPassbookDocument','ag-bank'],['ownerIdDocument','ag-ownerid'],['shopActDocument','ag-shop'],['gstCertificate','ag-gst'],['mtdcTourismCertificate','ag-mtdc'],['partnershipOrIncorporationCertificate','ag-inc'],['commercialVehicleDocuments','ag-commercial'],['iataIrctcLicense','ag-iata'],['agencyLogo','ag-logo']].forEach(([k,id])=>{const f=document.getElementById(id)?.files?.[0];if(f)fd.append(k,f)});const b=document.getElementById('ag-submit');try{b.disabled=true;b.textContent='Uploading documents...';const r=await API.multipart('/api/partners/agency/register',fd);toast(r.message);go('login')}catch(err){toast(err.message)}finally{b.disabled=false;b.textContent='Submit for Verification'}}
function driverRegister(){return `${header()}<main class="auth-wrap"><div class="auth-card" style="max-width:900px"><h1>Driver Registration</h1><p class="muted">Select the exact vehicle model you will use. The same verified vehicle will appear on your trips and determine the customer fare.</p><form onsubmit="driverRegisterSubmit(event)"><div class="form-grid"><div class="field"><label>FULL NAME *</label><input id="dr-name" required></div><div class="field"><label>EMAIL *</label><input id="dr-email" type="email" required></div><div class="field"><label>MOBILE *</label><input id="dr-phone" inputmode="numeric" pattern="[6-9][0-9]{9}" maxlength="10" required></div><div class="field"><label>PASSWORD *</label><input id="dr-pass" type="password" minlength="8" required></div><div class="field"><label>DATE OF BIRTH</label><input id="dr-dob" type="date"></div><div class="field full"><label>ADDRESS</label><textarea id="dr-address" rows="2"></textarea></div><div class="field"><label>EMERGENCY CONTACT NAME *</label><input id="dr-ec-name" required></div><div class="field"><label>EMERGENCY CONTACT NUMBER *</label><input id="dr-ec-phone" inputmode="numeric" pattern="[6-9][0-9]{9}" maxlength="10" required></div><div class="field"><label>RELATION *</label><select id="dr-ec-rel"><option>Parent</option><option>Spouse</option><option>Sibling</option><option>Friend</option><option>Other</option></select></div><div class="field"><label>VEHICLE REGISTRATION NUMBER *</label><input id="dr-vehicle-no" placeholder="MH12AB1234" required></div><div class="field"><label>VEHICLE MODEL *</label><select id="dr-type" required onchange="updateSeatOptions()"><option value="">Select vehicle</option>${(VEHICLE_CATALOG||[]).map(v=>`<option value="${esc(v.vehicleType)}">${esc(v.vehicleType)} · ${v.capacity} seats</option>`).join('')}</select></div><div class="field"><label>SEATING CAPACITY *</label><select id="dr-seats" required><option value="">Select vehicle first</option></select></div></div><h3 style="margin-top:24px">KYC Documents</h3><div class="form-grid">${fileField('dr-aadhaar','Aadhaar Card *',true)}${fileField('dr-dl','Driving Licence *',true)}${fileField('dr-pan','PAN Card')}${fileField('dr-address-proof','Address Proof')}</div><h3 style="margin-top:24px">Vehicle Documents</h3><div class="form-grid">${fileField('dr-rc','RC *',true)}${fileField('dr-insurance','Insurance *',true)}${fileField('dr-puc','PUC *',true)}${fileField('dr-permit','Commercial Permit — If Applicable')}${fileField('dr-fitness','Fitness Certificate — If Applicable')}${fileField('dr-photo','Vehicle Photo')}</div><label class="check" style="margin:18px 0"><input type="checkbox" required> I confirm the documents and vehicle details are genuine.</label><button class="btn btn-primary" style="width:100%" id="dr-submit">Submit for Verification</button></form></div></main>${footer()}`}
function hydrateDriverVehicleOptions(){const el=document.getElementById('dr-type');if(!el)return;const current=el.value;el.innerHTML='<option value="">Select vehicle</option>'+(VEHICLE_CATALOG||[]).map(v=>`<option value="${esc(v.vehicleType)}">${esc(v.vehicleType)} · ${v.capacity} seats</option>`).join('');if(current)el.value=current;updateSeatOptions();}
function updateSeatOptions(){const type=document.getElementById('dr-type')?.value;const v=(VEHICLE_CATALOG||[]).find(x=>x.vehicleType===type);const el=document.getElementById('dr-seats');if(!el)return;el.innerHTML=v?`<option value="${v.capacity}">${v.capacity} Seats</option>`:'<option value="">Select vehicle first</option>';}
async function driverRegisterSubmit(e){e.preventDefault();const fd=new FormData();const vals={fullName:'dr-name',email:'dr-email',phone:'dr-phone',password:'dr-pass',dateOfBirth:'dr-dob',address:'dr-address',emergencyContactName:'dr-ec-name',emergencyContactNumber:'dr-ec-phone',emergencyContactRelation:'dr-ec-rel',vehicleRegistrationNumber:'dr-vehicle-no',vehicleType:'dr-type',seatingCapacity:'dr-seats'};Object.entries(vals).forEach(([k,id])=>{const v=document.getElementById(id)?.value;if(v)fd.append(k,v)});[['aadhaarDocument','dr-aadhaar'],['drivingLicenceDocument','dr-dl'],['panDocument','dr-pan'],['addressProofDocument','dr-address-proof'],['rcDocument','dr-rc'],['insuranceDocument','dr-insurance'],['pucDocument','dr-puc'],['commercialPermitDocument','dr-permit'],['fitnessCertificateDocument','dr-fitness'],['vehiclePhoto','dr-photo']].forEach(([k,id])=>{const f=document.getElementById(id)?.files?.[0];if(f)fd.append(k,f)});const b=document.getElementById('dr-submit');try{b.disabled=true;b.textContent='Uploading documents...';const r=await API.multipart('/api/partners/driver/register',fd);toast(r.message);go('login')}catch(err){toast(err.message)}finally{b.disabled=false;b.textContent='Submit for Verification'}}
async function hotelCheckIn(id){if(!confirm('Confirm guest check-in?'))return;try{await API.post(`/api/property-bookings/${id}/check-in`,{});toast('Guest checked in successfully.');loadHotelDashboard();}catch(e){toast(e.message||'Check-in failed.')}}
async function hotelCheckOut(id){if(!confirm('Confirm guest check-out?'))return;try{await API.post(`/api/property-bookings/${id}/check-out`,{});toast('Guest checked out successfully.');loadHotelDashboard();}catch(e){toast(e.message||'Check-out failed.')}}

function complaint(){if(!state.user)return loginPrompt('Login to submit a complaint.');return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Help & Complaint</h1><p class="section-sub">Send an issue with photo/video proof. Admin will handle it.</p></div></div><section class="section"><div class="container"><div class="panel" style="max-width:850px;margin:auto"><form onsubmit="complaintSubmit(event)"><div class="form-grid"><div class="field"><label>CATEGORY *</label><select id="cp-category" required><option>Driver Behaviour</option><option>Trip Issue</option><option>Booking Issue</option><option>Property/Stay Issue</option><option>Payment Issue</option><option>Safety Issue</option><option>Fraud / Suspicious Activity</option><option>Other</option></select></div><div class="field"><label>PRIORITY</label><select id="cp-priority"><option>NORMAL</option><option>LOW</option><option>HIGH</option><option>URGENT</option></select></div><div class="field full"><label>SUBJECT *</label><input id="cp-subject" required></div><div class="field full"><label>DESCRIPTION *</label><textarea id="cp-desc" rows="6" required></textarea></div><div class="field"><label>RELATED TRIP ID</label><input id="cp-trip" type="number"></div><div class="field"><label>RELATED BOOKING ID</label><input id="cp-booking" type="number"></div><div class="field"><label>RELATED PROPERTY ID</label><input id="cp-property" type="number"></div><div class="field full"><label>PHOTO / VIDEO PROOF</label><input id="cp-files" type="file" multiple accept="image/*,video/*"><small class="muted">Photo max 10 MB each · Video max 50 MB each</small></div></div><button class="btn btn-primary" id="cp-submit">Submit Complaint</button></form></div></div></section></main>${footer()}`}
async function complaintSubmit(e){e.preventDefault();const fs=[...(document.getElementById('cp-files').files||[])];if(fs.length>10){toast('Maximum 10 attachments are allowed.');return}for(const f of fs){const video=f.type.startsWith('video/');const max=video?50:10;if(f.size>max*1024*1024){toast(`${f.name} is larger than ${max} MB`);return}}const fd=new FormData();[['category','cp-category'],['subject','cp-subject'],['description','cp-desc'],['priority','cp-priority'],['relatedTripId','cp-trip'],['relatedBookingId','cp-booking'],['relatedPropertyId','cp-property']].forEach(([k,id])=>{const v=document.getElementById(id).value;if(v)fd.append(k,v)});fs.forEach(f=>fd.append('filesIn',f));const b=document.getElementById('cp-submit');try{b.disabled=true;b.textContent='Submitting...';await API.multipart('/api/complaints',fd);toast('Complaint submitted successfully.');go('dashboard')}catch(err){toast(err.message)}finally{b.disabled=false;b.textContent='Submit Complaint'}}
function safety(){
  return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Safety & SOS</h1><p class="section-sub">Your emergency contact and location are used only when you request help.</p></div></div>
  <section class="section"><div class="container safety-layout">
    <div class="panel sos-panel">
      <div class="sos-icon">SOS</div>
      <div class="sos-copy"><span class="eyebrow danger-eyebrow">Emergency assistance</span><h2>Need urgent help?</h2><p class="muted">Press and hold the SOS button for 2 seconds. We will capture your current GPS and create an alert using your saved emergency contact.</p></div>
      <div id="sos-contact-status" class="sos-contact-status">${loading("Checking emergency contact...")}</div>
      <button id="sos-hold-btn" class="sos-hold-btn" type="button" onpointerdown="startSOSHold(event)" onpointerup="finishSOSHold(event)" onpointercancel="cancelSOSHold()" onpointerleave="cancelSOSHold()">
        <span class="sos-ring"></span><span class="sos-button-content"><strong>HOLD FOR SOS</strong><small>2 seconds</small></span>
      </button>
      <button class="btn btn-outline full-btn" type="button" onclick="callEmergency()">Call saved emergency contact</button>
      <p class="sos-note">If your contact is not added, SOS will ask you to add one first. GPS permission is required.</p>
    </div>
    <div class="panel">
      <h3> Safety checklist</h3>
      <div class="safety-list"><div><span>✓</span><p>Keep your emergency contact up to date.</p></div><div><span>✓</span><p>Allow location access when using SOS.</p></div><div><span>✓</span><p>Use SOS only for urgent situations.</p></div></div>
      <button class="btn btn-secondary full-btn" onclick="go('profile')">Manage emergency contact</button>
    </div>
    <div class="panel">
      <h3>Need help, not an emergency?</h3><p class="muted">Send a complaint or support request and our team can review it.</p>
      <button class="btn btn-primary full-btn" onclick="go('complaint')">Create complaint</button>
      <button class="btn btn-outline full-btn" style="margin-top:10px" onclick="go('stays')">Find a nearby stay</button>
    </div>
  </div></section></main>${footer()}`
}

let sosHoldTimer = null;
let sosHoldTriggered = false;

async function getEmergencyContactForCurrentUser() {
  if (state.user?.role === "DRIVER") {
    return API.get("/api/driver/emergency-contact");
  }
  return API.get("/api/users/profile");
}

async function loadSafetyContact() {
  const el = document.getElementById("sos-contact-status");
  if (!el || !state.user) return;
  try {
    const user = await getEmergencyContactForCurrentUser();
    state.currentProfile = user;
    if (user.emergencyContactName && user.emergencyContactNumber) {
      el.innerHTML = `<span class="contact-ok">Saved emergency contact</span><strong>${esc(user.emergencyContactName)}</strong><small>${esc(user.emergencyContactNumber)}${user.emergencyContactRelation ? " · " + esc(user.emergencyContactRelation) : ""}</small>`;
    } else {
      el.innerHTML = `<span class="contact-missing">! Emergency contact not added</span><strong>Add one before SOS</strong><small>Your contact is managed from Profile.</small><button class="btn btn-primary btn-sm" onclick="go('profile')">Add emergency contact</button>`;
    }
  } catch (error) {
    el.innerHTML = `<span class="contact-missing">! Could not load contact</span><small>${esc(error.message || "Please try again.")}</small>`;
  }
}

function startSOSHold(event) {
  event.preventDefault();
  sosHoldTriggered = false;
  const btn = document.getElementById("sos-hold-btn");
  if (!btn || btn.disabled) return;
  btn.classList.add("holding");
  sosHoldTimer = setTimeout(async () => {
    sosHoldTriggered = true;
    btn.classList.remove("holding");
    await activateSOS();
  }, 2000);
}

function finishSOSHold(event) {
  event.preventDefault();
  if (sosHoldTimer) clearTimeout(sosHoldTimer);
  sosHoldTimer = null;
  const btn = document.getElementById("sos-hold-btn");
  if (btn) btn.classList.remove("holding");
  if (!sosHoldTriggered) toast("Hold the SOS button for 2 seconds.");
}

function cancelSOSHold() {
  if (sosHoldTimer) clearTimeout(sosHoldTimer);
  sosHoldTimer = null;
  const btn = document.getElementById("sos-hold-btn");
  if (btn) btn.classList.remove("holding");
}

async function getPosition(){
  return new Promise((resolve)=>{
    if(!navigator.geolocation) return resolve(null);
    navigator.geolocation.getCurrentPosition(
      p=>resolve({latitude:p.coords.latitude,longitude:p.coords.longitude,accuracy:p.coords.accuracy}),
      ()=>resolve(null),
      {enableHighAccuracy:true,timeout:10000,maximumAge:30000}
    );
  });
}

async function activateSOS(){
  if(!state.user){toast("Please login first.");return}

  let profile;
  try {
    profile = await getEmergencyContactForCurrentUser();
    state.currentProfile = profile;
  } catch (error) {
    toast(error.message || "Could not load your emergency contact.");
    return;
  }

  if (!profile.emergencyContactName || !profile.emergencyContactNumber) {
    toast("Please add an emergency contact before using SOS.");
    go("profile");
    return;
  }

  const pos = await getPosition();
  if (!pos) {
    toast("Location access is required for SOS. Please allow GPS and try again.");
    return;
  }

  try {
    await API.post("/api/sos", pos);
    toast(`SOS activated. Calling ${profile.emergencyContactName}...`);
    setTimeout(() => {
      location.href = `tel:+91${profile.emergencyContactNumber}`;
    }, 450);
  } catch(err) {
    if (/can't reach|backend|network|failed to fetch/i.test(err.message || "")) {
      localStorage.setItem("spt_pending_sos", JSON.stringify({...pos, createdAt:new Date().toISOString()}));
      toast("Network unavailable. SOS location saved on this device; reconnect to sync.");
      return;
    }
    toast(err.message || "Could not activate SOS.");
  }
}

async function callEmergency(){
  if(!state.user){toast("Please login first.");return}
  try {
    const user = await getEmergencyContactForCurrentUser();
    if(!user.emergencyContactNumber){
      toast("Please add an emergency contact first.");
      go("profile");
      return;
    }
    location.href=`tel:+91${user.emergencyContactNumber}`;
  } catch(error) {
    toast(error.message || "Could not load emergency contact.");
  }
}

async function syncPendingSOS(){
  if(!state.user)return;
  const raw=localStorage.getItem("spt_pending_sos");
  if(!raw)return;
  try{
    const pending=JSON.parse(raw);
    await API.post("/api/sos",pending);
    localStorage.removeItem("spt_pending_sos");
    toast("Pending SOS synced successfully.");
  }catch(error){
    if (!/can't reach|backend|network|failed to fetch/i.test(error.message || "")) {
      localStorage.removeItem("spt_pending_sos");
    }
  }
}

function returnPage() {
  return `${header()}<main class="page"><div class="page-header"><div class="container"><h1>Return Trip Seeker</h1><p class="section-sub">Don't let the vehicle return empty. Find your ride back.</p></div></div><section class="section"><div class="container"><div class="empty"><p>Return-trip matching is not a separate booking flow yet.</p><button class="btn btn-primary" onclick="go('trips')">Search available return trips →</button></div></div></section></main>${footer()}` }

function partner() {
  return `${header()}<main class="page partner-page">
    <div class="page-header"><div class="container">
      <span class="eyebrow page-eyebrow">PARTNER WITH SAHYADRI</span>
      <h1>Grow your travel business with Sahyadri</h1>
      <p class="section-sub">Choose the partner path that matches your service. Verification keeps the platform reliable for travelers.</p>
    </div></div>
    <section class="section"><div class="container">
      <div class="partner-grid">
        <article class="partner-card"><div class="partner-icon">DR</div><span class="info-kicker">TRANSPORT</span><h2>Driver Partner</h2><p>Publish shared trips, manage seats and serve travelers across Maharashtra.</p><button class="btn btn-primary" onclick="go('driver-register')">Driver Registration</button></article>
        <article class="partner-card"><div class="partner-icon">AG</div><span class="info-kicker">BUSINESS</span><h2>Travel Agency</h2><p>Submit your agency details and required documents for admin verification.</p><button class="btn btn-secondary" onclick="go('agency-register')">Agency Registration</button></article>
        <article class="partner-card"><div class="partner-icon">ST</div><span class="info-kicker">STAYS</span><h2>Hotel / Stay Owner</h2><p>Register your hotel or homestay for an admin-reviewed stay partner account.</p><button class="btn btn-outline" onclick="go('hotel-register')">Hotel Registration</button></article>
      </div>
      <div class="partner-note panel"><strong>Professional onboarding</strong><span>Partner applications are reviewed before partner-only features are enabled.</span></div>
    </div></section>
  </main>${footer()}`;
}

function contact() {
  return `${header()}<main class="page contact-page">
    <div class="page-header"><div class="container">
      <span class="eyebrow page-eyebrow">GET IN TOUCH</span>
      <h1>Contact Sahyadri Pool & Trip</h1>
      <p class="section-sub">Questions, booking support, partnerships or feedback — send us a message directly from the website.</p>
    </div></div>
    <section class="section"><div class="container contact-layout">
      <aside class="contact-info-panel panel">
        <span class="info-kicker">SAHYADRI POOL & TRIP</span>
        <h2>We’re here to help.</h2>
        <p class="muted">For support, partnership enquiries and general questions, use the form. Your message is submitted to the platform support inbox.</p>
        <div class="contact-detail-list">
          <a href="tel:+917620815801"><span class="contact-detail-icon">☎</span><div><small>PHONE</small><strong>+91 76208 15801</strong></div></a>
          <a href="mailto:vishalshingte761@gmail.com"><span class="contact-detail-icon">✉</span><div><small>EMAIL</small><strong>vishalshingte761@gmail.com</strong></div></a>
          <div><span class="contact-detail-icon">⌖</span><div><small>OFFICE / BASE</small><strong>A/P Ranjani, Ambegaon<br>Pune, Maharashtra 410504</strong></div></div>
          <div><span class="contact-detail-icon">◷</span><div><small>SUPPORT HOURS</small><strong>Monday – Saturday<br>8:00 AM – 8:00 PM</strong></div></div>
        </div>
      </aside>
      <section class="contact-form-panel panel">
        <div class="panel-title-row"><div><span class="info-kicker">SEND A MESSAGE</span><h2>How can we help?</h2><p class="muted">We’ll receive your message directly. No email application is required.</p></div></div>
        <form onsubmit="sendContact(event)" class="contact-form">
          <div class="form-grid"><div class="field"><label>NAME *</label><input id="contact-name" required autocomplete="name" placeholder="Your full name"></div>
          <div class="field"><label>EMAIL *</label><input id="contact-email" type="email" required autocomplete="email" placeholder="you@example.com"></div>
          <div class="field"><label>PHONE</label><input id="contact-phone" inputmode="tel" maxlength="10" autocomplete="tel" placeholder="10-digit mobile number"></div>
          <div class="field"><label>SUBJECT *</label><input id="contact-subject" required placeholder="How can we help?"></div>
          <div class="field full"><label>MESSAGE *</label><textarea id="contact-message" rows="7" required maxlength="5000" placeholder="Write your message here..."></textarea></div></div>
          <button class="btn btn-primary contact-submit" type="submit" id="contact-submit-btn">Send Message</button>
          <p class="form-hint">Your message is sent securely to the Sahyadri support inbox through the website backend.</p>
        </form>
      </section>
    </div></section>
  </main>${footer()}`;
}

async function sendContact(e) {
  e.preventDefault();
  const btn=document.getElementById("contact-submit-btn");
  const body={
    name:document.getElementById("contact-name")?.value.trim(),
    email:document.getElementById("contact-email")?.value.trim(),
    phone:document.getElementById("contact-phone")?.value.trim(),
    subject:document.getElementById("contact-subject")?.value.trim(),
    message:document.getElementById("contact-message")?.value.trim()
  };
  if(!body.name||!body.email||!body.subject||!body.message){toast("Please complete all required fields.");return;}
  try{
    if(btn){btn.disabled=true;btn.textContent="Sending…";}
    const result=await API.post("/api/contact",body);
    toast(result?.message||"Message sent successfully.");
    document.querySelector(".contact-form")?.reset();
  }catch(error){toast(error.message||"Could not send your message.");}
  finally{if(btn){btn.disabled=false;btn.textContent="Send Message";}}
}

function hotelRegister() {
  return `${header()}<main class="page">
    <div class="page-header"><div class="container">
      <span class="eyebrow page-eyebrow">STAY PARTNER ONBOARDING</span>
      <h1>Hotel & Homestay Registration</h1>
      <p class="section-sub">Submit your property and owner details. Admin approval is required before a Stay Owner account can publish properties.</p>
    </div></div>
    <section class="section"><div class="container"><div class="auth-card partner-registration-card">
      <form onsubmit="hotelRegisterSubmit(event)">
        <div class="form-section-heading"><span class="info-kicker">OWNER DETAILS</span><h2>Your account</h2></div>
        <div class="form-grid">
          <div class="field"><label>OWNER / CONTACT NAME *</label><input id="hotel-owner-name" required autocomplete="name"></div>
          <div class="field"><label>EMAIL *</label><input id="hotel-email" type="email" required autocomplete="email"></div>
          <div class="field"><label>MOBILE *</label><input id="hotel-phone" inputmode="numeric" pattern="[6-9][0-9]{9}" maxlength="10" required autocomplete="tel"></div>
          <div class="field"><label>PASSWORD *</label><input id="hotel-password" type="password" minlength="8" required autocomplete="new-password"></div>
        </div>
        <div class="form-section-heading"><span class="info-kicker">PROPERTY DETAILS</span><h2>Tell us about your stay</h2></div>
        <div class="form-grid">
          <div class="field"><label>PROPERTY / HOTEL NAME *</label><input id="hotel-property-name" required placeholder="e.g. Sahyadri Valley Stay"></div>
          <div class="field"><label>PROPERTY TYPE *</label><select id="hotel-property-type"><option value="HOTEL">Hotel</option><option value="HOMESTAY">Homestay</option><option value="LODGE">Lodge</option><option value="RESORT">Resort</option><option value="GUEST HOUSE">Guest House</option></select></div>
          <div class="field full"><label>ADDRESS *</label><textarea id="hotel-address" rows="3" required placeholder="Complete property address"></textarea></div>
          <div class="field"><label>CITY / DISTRICT *</label><input id="hotel-city" required placeholder="Pune"></div>
          <div class="field"><label>TOTAL ROOMS *</label><input id="hotel-rooms" type="number" min="1" max="10000" required></div>
          <div class="field"><label>STARTING PRICE / NIGHT (₹) *</label><input id="hotel-price" type="number" min="0" step="0.01" required></div>
          <div class="field full"><label>PROPERTY DESCRIPTION *</label><textarea id="hotel-description" rows="5" required maxlength="3000" placeholder="Describe rooms, facilities, location and guest experience."></textarea></div>
        </div>
        <label class="check" style="margin:18px 0"><input id="hotel-consent" type="checkbox" required> I confirm that the information is accurate and I agree to admin verification before activation.</label>
        <button class="btn btn-primary" id="hotel-submit-btn" type="submit">Submit Registration</button>
      </form>
    </div></div></section>
  </main>${footer()}`;
}

async function hotelRegisterSubmit(e){
  e.preventDefault();
  const btn=document.getElementById("hotel-submit-btn");
  const body={
    ownerName:document.getElementById("hotel-owner-name").value.trim(),
    email:document.getElementById("hotel-email").value.trim(),
    phone:document.getElementById("hotel-phone").value.trim(),
    password:document.getElementById("hotel-password").value,
    propertyName:document.getElementById("hotel-property-name").value.trim(),
    propertyType:document.getElementById("hotel-property-type").value,
    address:document.getElementById("hotel-address").value.trim(),
    cityDistrict:document.getElementById("hotel-city").value.trim(),
    totalRooms:Number(document.getElementById("hotel-rooms").value),
    pricePerNight:Number(document.getElementById("hotel-price").value),
    description:document.getElementById("hotel-description").value.trim()
  };
  try{
    if(btn){btn.disabled=true;btn.textContent="Submitting…";}
    const result=await API.post("/api/partners/hotel/register",body);
    toast(result?.message||"Registration submitted for admin review.");
    setTimeout(()=>go("login"),900);
  }catch(error){toast(error.message||"Could not submit hotel registration.");}
  finally{if(btn){btn.disabled=false;btn.textContent="Submit Registration";}}
}

function how(){return infoPage("How It Works","HOW SAHYADRI WORKS",`<h2>One simple flow for shared travel.</h2><p>Discover trips, stays and Maharashtra forts, review the available information, and use the booking or navigation tools that match your journey.</p><div class="info-grid"><div><span class="info-kicker">01</span><strong>Discover</strong><p class="muted">Search live trips, stays and fort information.</p></div><div><span class="info-kicker">02</span><strong>Review</strong><p class="muted">Check route, source, availability and verification details.</p></div><div><span class="info-kicker">03</span><strong>Book or Navigate</strong><p class="muted">Book when available or open Google Maps for directions.</p></div><div><span class="info-kicker">04</span><strong>Travel Safely</strong><p class="muted">Keep emergency details updated and use SOS for urgent situations.</p></div></div>`);}


// =====================================================
// INDIA NATURE + SPECIAL HERITAGE REGISTRY
// 2100-row draft discovery registry: Maharashtra priority,
// 12 Jyotirlingas, UNESCO/World Heritage, caves and India-wide nature.
// Photos/info are resolved from the destination name at runtime.
// =====================================================
let NATURE_DESTINATIONS = [];
let natureSection = "INDIA_ALL";
let natureSearchTerm = "";

function indiaNature(){
  return `${header()}<main class="page"><div class="page-header"><div class="container"><span class="eyebrow page-eyebrow">BHARAT DESTINATIONS</span><h1>Explore Bharat Destinations</h1><p class="section-sub">Discover destinations beyond Maharashtra — nature, sacred places, caves and heritage locations arranged by classic travel categories.</p></div></div>
  <section class="section"><div class="container">
    <div class="nature-special-tabs">
      <button class="btn ${natureSection==='INDIA_ALL'?'btn-primary':'btn-outline'}" onclick="setNatureSection('INDIA_ALL')">All Bharat</button>
      <button class="btn ${natureSection==='INDIA_NATURE'?'btn-primary':'btn-outline'}" onclick="setNatureSection('INDIA_NATURE')">Nature & Escapes</button>
      <button class="btn ${natureSection==='JYOTIRLINGA'?'btn-primary':'btn-outline'}" onclick="setNatureSection('JYOTIRLINGA')">Sacred Jyotirlingas</button>
      <button class="btn ${natureSection==='WORLD_HERITAGE'?'btn-primary':'btn-outline'}" onclick="setNatureSection('WORLD_HERITAGE')">World Heritage</button>
      <button class="btn ${natureSection==='BIKE_ROUTES'?'btn-primary':'btn-outline'}" onclick="setNatureSection('BIKE_ROUTES')">🏍️ Biker Routes</button>
    </div>
    <div class="search-card nature-search-card"><div class="field"><label>SEARCH LOCATION</label><input id="nature-search" value="${esc(natureSearchTerm)}" oninput="filterNatureDestinations()" placeholder="Search destination, state, category or locality…"></div></div>
    <div id="nature-summary" class="nature-summary"></div>
    <div id="nature-list" class="grid grid-3">${loading('Loading destination registry…')}</div>
  </div></section></main>${footer()}`;
}
async function loadNatureDestinations(){
  const el=document.getElementById('nature-list'); if(!el)return;
  try{const data=await API.get('/api/destinations');NATURE_DESTINATIONS=Array.isArray(data)?data:[];window.NATURE_DESTINATIONS=NATURE_DESTINATIONS;filterNatureDestinations();}
  catch(e){el.innerHTML=errorBlock(e.message||'Unable to load India destination registry.');}
}
function setNatureSection(section){natureSection=section;render();}
function isMaharashtraDestination(d){
  return String(d?.state||'').trim().toLowerCase() === 'maharashtra';
}
function natureDisplaySection(section){
  return ({INDIA_ALL:'All Bharat',INDIA_NATURE:'Nature & Escapes',JYOTIRLINGA:'Sacred Jyotirlingas',WORLD_HERITAGE:'World Heritage'})[section] || String(section||'DESTINATIONS').replaceAll('_',' ');
}
const BIKE_ROUTES = [
  {id:'BR-001',name:'Manali – Leh Highway',start:'Manali',end:'Leh',state:'Himachal Pradesh / Ladakh',distanceKm:474,difficulty:'Hard',season:'June – September',highlights:'High-altitude passes, Gata Loops, Sarchu, Morey Plains and Tanglang La.',photoQuery:'Manali Leh Highway motorcycle',source:'https://www.incredibleindia.gov.in/en/trips/trip-listing/shimla-manali-leh-srinagar-circuit'},
  {id:'BR-002',name:'Srinagar – Leh Highway',start:'Srinagar',end:'Leh',state:'Jammu & Kashmir / Ladakh',distanceKm:434,difficulty:'Moderate–Hard',season:'June – October',highlights:'Sonamarg, Zoji La, Drass, Kargil, Namika La and Fotu La.',photoQuery:'Srinagar Leh Highway motorcycle',source:'https://www.incredibleindia.gov.in/en/trips/trip-listing/srinagar-to-manali-via-ladakh'},
  {id:'BR-003',name:'Leh – Nubra Valley',start:'Leh',end:'Diskit / Nubra',state:'Ladakh',distanceKm:120,difficulty:'Hard',season:'June – September',highlights:'Khardung La approach, mountain desert, Diskit and Nubra Valley.',photoQuery:'Nubra Valley Khardung La motorcycle',source:'https://www.jadomoto.com/blogs/guides/best-motorcycle-roads-india'},
  {id:'BR-004',name:'Nubra – Pangong Lake',start:'Nubra Valley',end:'Pangong Lake',state:'Ladakh',distanceKm:160,difficulty:'Hard',season:'June – September',highlights:'Remote high-altitude road connecting two iconic Ladakh landscapes.',photoQuery:'Nubra Pangong road motorcycle',source:'https://www.tripqube.com/blog/leh-ladakh-motorcycle-trip-guide.html'},
  {id:'BR-005',name:'Leh – Umling La – Leh',start:'Leh',end:'Umling La',state:'Ladakh',distanceKm:240,difficulty:'Extreme',season:'June – September',highlights:'High-altitude ride toward Umling La and the Hanle sector; weather and access can change.',photoQuery:'Umling La motorcycle Ladakh',source:'https://www.jadomoto.com/blogs/guides/best-motorcycle-roads-india'},
  {id:'BR-006',name:'Spiti Valley Circuit',start:'Shimla',end:'Kaza / Manali',state:'Himachal Pradesh',distanceKm:450,difficulty:'Hard',season:'May – October',highlights:'Cold-desert roads, Nako, Tabo, Kaza and Kunzum Pass corridor.',photoQuery:'Spiti Valley motorcycle road',source:'https://www.jadomoto.com/blogs/guides/best-motorcycle-roads-india'},
  {id:'BR-007',name:'Kargil – Padum Road',start:'Kargil',end:'Padum',state:'Ladakh',distanceKm:235,difficulty:'Hard',season:'June – September',highlights:'Zanskar landscape and Pensi La mountain corridor.',photoQuery:'Kargil Padum Zanskar motorcycle road',source:'https://www.jadomoto.com/blogs/guides/best-motorcycle-roads-india'},
  {id:'BR-008',name:'Rajasthan Desert Circuit',start:'Jaipur',end:'Jaipur',state:'Rajasthan',distanceKm:575,difficulty:'Moderate',season:'October – March',highlights:'Jaipur, Pushkar, Jodhpur, Jaisalmer and Bikaner through the Thar and Aravalli landscapes.',photoQuery:'Rajasthan desert motorcycle road Jaisalmer',source:'https://www.jadomoto.com/blogs/guides/rajasthan-motorcycle-guide'},
  {id:'BR-009',name:'Kochi – Munnar',start:'Kochi',end:'Munnar',state:'Kerala',distanceKm:130,difficulty:'Moderate',season:'September – February',highlights:'Western Ghats climb, tea estates and rainforest scenery.',photoQuery:'Kochi Munnar motorcycle road',source:'https://www.jadomoto.com/blogs/guides/best-motorcycle-roads-india'},
  {id:'BR-010',name:'Goa – Gokarna Coastal Ride',start:'Goa',end:'Gokarna',state:'Goa / Karnataka',distanceKm:240,difficulty:'Moderate',season:'October – March',highlights:'Konkan coast, beaches, forest stretches and scenic coastal roads.',photoQuery:'Goa Gokarna coastal road motorcycle',source:'https://www.jadomoto.com/blogs/guides/best-motorcycle-roads-india'},
  {id:'BR-011',name:'Guwahati – Tawang',start:'Guwahati',end:'Tawang',state:'Assam / Arunachal Pradesh',distanceKm:450,difficulty:'Hard',season:'October – April',highlights:'Long Himalayan ride through Bomdila and Sela Pass toward Tawang.',photoQuery:'Guwahati Tawang motorcycle road',source:'https://www.incredibleindia.gov.in/'},
  {id:'BR-012',name:'Shillong – Cherrapunji – Mawlynnong',start:'Shillong',end:'Mawlynnong',state:'Meghalaya',distanceKm:180,difficulty:'Moderate',season:'October – April',highlights:'Cloud country, waterfalls, winding roads and Khasi hills.',photoQuery:'Meghalaya Shillong Cherrapunji motorcycle road',source:'https://www.incredibleindia.gov.in/'},
  {id:'BR-013',name:'Chennai – Mahabalipuram – Pondicherry',start:'Chennai',end:'Pondicherry',state:'Tamil Nadu / Puducherry',distanceKm:200,difficulty:'Easy–Moderate',season:'October – February',highlights:'East Coast Road, sea views, Mahabalipuram heritage and Pondicherry.',photoQuery:'East Coast Road Chennai Pondicherry motorcycle',source:'https://www.incredibleindia.gov.in/'},
  {id:'BR-014',name:'Mumbai – Goa Coastal Ride',start:'Mumbai',end:'Goa',state:'Maharashtra / Goa',distanceKm:590,difficulty:'Moderate–Hard',season:'October – February',highlights:'Konkan coast, ghats, sea-facing stretches and long-distance touring.',photoQuery:'Mumbai Goa coastal road motorcycle Konkan',source:'https://www.incredibleindia.gov.in/'},
  {id:'BR-015',name:'Bengaluru – Coorg – Wayanad',start:'Bengaluru',end:'Wayanad',state:'Karnataka / Kerala',distanceKm:300,difficulty:'Moderate',season:'October – February',highlights:'Coffee country, Western Ghats curves, forests and hill roads.',photoQuery:'Coorg Wayanad motorcycle road',source:'https://www.incredibleindia.gov.in/'}
];
async function loadBikeRoutes(){try{const rows=await API.get('/api/bike-road-trips');if(Array.isArray(rows)&&rows.length){window.BIKE_ROUTES_LIVE=rows.map(b=>({id:b.routeId,name:b.routeName,start:b.startPoint,end:b.endPoint,state:b.states,distanceKm:b.routeDistanceKm,difficulty:String(b.difficulty||'PENDING').replaceAll('_','–'),season:b.bestSeason||'PENDING',highlights:b.highlights||'PENDING',photoQuery:b.photoQuery||b.routeName,source:b.primarySource||'#'}));return window.BIKE_ROUTES_LIVE;}}catch(e){console.warn('bike route API',e)}return BIKE_ROUTES;}
function bikeRoutesPage(){const list=window.BIKE_ROUTES_LIVE||BIKE_ROUTES;return `<div class="bike-route-grid">${list.map(b=>`<article class="card bike-route-card"><div class="bike-route-photo" data-bike-photo="${esc(b.photoQuery)}"><span>🏍️</span><small>Loading ride view…</small></div><div class="card-body"><span class="tag">${esc(b.state)}</span><h3>${esc(b.name)}</h3><p class="muted">${esc(b.start)} → ${esc(b.end)}</p><div class="bike-route-meta"><span><b>${b.distanceKm} km</b> route</span><span>${esc(b.difficulty)}</span><span>${esc(b.season)}</span></div><p>${esc(b.highlights)}</p><div class="spot-actions"><button class="btn btn-primary btn-sm" onclick="openBikeRouteMap('${esc(b.start)}','${esc(b.end)}')">Open Route</button><a class="btn btn-outline btn-sm" href="${esc(b.source)}" target="_blank" rel="noopener noreferrer">Route source</a></div></div></article>`).join('')}</div>`;}
function openBikeRouteMap(start,end){window.open(`https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(start+', India')}&destination=${encodeURIComponent(end+', India')}&travelmode=driving`,'_blank','noopener');}
async function hydrateBikeRoutePhotos(){const nodes=[...document.querySelectorAll('[data-bike-photo]')];let i=0;const worker=async()=>{while(i<nodes.length){const n=nodes[i++],q=n.dataset.bikePhoto||'';try{const r=await fetch(`https://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=${encodeURIComponent(q)}&gsrnamespace=6&gsrlimit=6&prop=imageinfo&iiprop=url&iiurlwidth=1000&format=json&origin=*`);const j=await r.json();const page=Object.values(j.query?.pages||{})[0];const url=page?.imageinfo?.[0]?.thumburl||page?.imageinfo?.[0]?.url;if(url)n.innerHTML=`<img src="${esc(url)}" alt="${esc(q)}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">Wikimedia Commons</span>`;}catch(e){console.warn('bike photo',q,e)}}};await Promise.all([worker(),worker(),worker()]);}
function filterNatureDestinations(){
  natureSearchTerm=document.getElementById('nature-search')?.value.trim()||'';
  const q=natureSearchTerm.toLowerCase();
  if(natureSection==='BIKE_ROUTES'){const el=document.getElementById('nature-list');if(el){el.className='bike-route-list';el.innerHTML=bikeRoutesPage();loadBikeRoutes().then(list=>{const summary=document.getElementById('nature-summary');if(summary)summary.innerHTML='<strong>'+list.length+'</strong> classic long-distance motorcycle routes across India · distances are route-planning estimates and can vary by exact road/season.';el.innerHTML=bikeRoutesPage();hydrateBikeRoutePhotos();});}return;}
  // Bharat Destinations is strictly outside Maharashtra. Maharashtra records remain in Sahyadri.
  let list=natureSection==='INDIA_ALL' ? NATURE_DESTINATIONS.filter(x=>!isMaharashtraDestination(x)) : NATURE_DESTINATIONS.filter(x=>String(x.section||'')===natureSection).filter(x=>!isMaharashtraDestination(x));
  if(q) list=list.filter(x=>[x.name,x.state,x.district,x.locality,x.category,x.knownFor,x.description,x.section].some(v=>String(v||'').toLowerCase().includes(q)));
  const summary=document.getElementById('nature-summary'); if(summary) summary.innerHTML=q ? `<strong>${list.length}</strong> results in <strong>${esc(natureDisplaySection(natureSection))}</strong> · Search is limited to the selected region/category.` : `<strong>${list.length}</strong> destinations in <strong>${esc(natureDisplaySection(natureSection))}</strong> · Only destinations outside Maharashtra are shown in Bharat Destinations; Maharashtra remains in Sahyadri.`;
  const el=document.getElementById('nature-list'); if(el) el.innerHTML=list.length?list.map(natureCard).join(''):emptyBlock('No destination matches this category.');
  if(list.length) hydrateNaturePhotos();
}
function natureCard(d){
  const icon={WATERFALL:'💧',BEACH:'🏖️',CAVE:'🪨',HILL_STATION:'⛰️',NATURE:'🌿',NATURE_REGION:'🗺️',WILDLIFE:'🐾',LAKE:'🌊',VALLEY:'🏞️',PEAK:'🗻',ISLAND:'🏝️',GHAT:'⛰️',Jyotirlinga:'🕉️'}[d.category]||'🌿';
  return `<article class="card nature-card"><div class="place-photo live-nature-photo" data-nature-id="${esc(d.locationId||'')}" data-nature-photo="${esc(d.name)}"><span>${icon}</span><small>Loading destination photo…</small></div><div class="card-body"><div class="spot-tags"><span class="tag">${esc(d.category||'Nature')}</span><span class="tag">${esc(d.popularityTier||'PENDING')}</span></div><h3>${esc(d.name)}</h3><p class="muted">${esc(d.state||'PENDING')}${d.district?' · '+esc(d.district):''}</p><p>${esc(d.knownFor||d.description||'Information pending verified source match.')}</p><div class="spot-meta"><span>${esc(d.verificationStatus||'PENDING')}</span><span>${esc(d.recordStatus||'PENDING')}</span></div><button class="btn btn-secondary btn-sm" onclick="openNatureDetails('${encodeURIComponent(d.locationId||'')}')">View Details</button></div></article>`;
}
function haversineKm(lat1,lon1,lat2,lon2){const R=6371,rad=Math.PI/180;const dLat=(lat2-lat1)*rad,dLon=(lon2-lon1)*rad;const a=Math.sin(dLat/2)**2+Math.cos(lat1*rad)*Math.cos(lat2*rad)*Math.sin(dLon/2)**2;return R*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));}
function natureDetailRow(label,value){return `<div class="spot-detail-row"><span>${esc(label)}</span><b>${esc(value===null||value===undefined||String(value).trim()===''?'PENDING':value)}</b></div>`;}
async function resolveNatureCoordinates(d){
  const resolved=await resolveDestinationTarget(d);
  return resolved ? {lat:resolved.lat,lng:resolved.lng,display:resolved.display,source:resolved.source} : null;
}
async function requestNatureDistance(encodedId){
  const out=document.getElementById('nature-distance-value'); if(!out)return;
  const id=decodeURIComponent(encodedId||''); const d=NATURE_DESTINATIONS.find(x=>String(x.locationId)===id); if(!d){out.textContent='PENDING';return;}
  out.textContent='Calculating road distance…';
  try{
    const resolved=await resolveDestinationTarget(d); if(!resolved){out.textContent='Reliable map reference unavailable';return;}
    const pos=await getUserPosition(); const routed=await fetchRoadDistanceKm(pos.coords.latitude,pos.coords.longitude,resolved.lat,resolved.lng);
    if(routed){out.textContent=`${routed.km.toFixed(1)} km from your current location`;return;}
    const straight=haversineKm(pos.coords.latitude,pos.coords.longitude,resolved.lat,resolved.lng);
    if(!Number.isFinite(straight)||straight<=0||straight>3500){out.textContent='Reliable road distance unavailable';return;}
    out.textContent=`≈ ${(Math.min(straight*1.22,4500)).toFixed(1)} km planning estimate from your current location`;
  }catch(e){out.textContent='Reliable road distance unavailable';}
}
async function openNatureDetails(encodedId){
  const id=decodeURIComponent(encodedId||'');
  const d=NATURE_DESTINATIONS.find(x=>String(x.locationId)===id);
  if(!d){toast('Destination details are not available.');return;}
  const lat=Number(d.latitude),lng=Number(d.longitude),hasGps=Number.isFinite(lat)&&Number.isFinite(lng);
  document.body.insertAdjacentHTML('beforeend',`<div class="modal-backdrop" id="nature-modal"><div class="modal nature-detail-modal"><div class="modal-head"><div><span class="eyebrow page-eyebrow">${esc(String(d.section||'DESTINATION').replaceAll('_',' '))}</span><h2>${esc(d.name)}</h2></div><button class="close" onclick="document.getElementById('nature-modal')?.remove()">×</button></div><div class="place-photo live-detail-photo" id="nature-detail-photo"><span>🌿</span><small>Loading destination photo…</small></div><div class="nature-detail-grid"><div class="panel"><h3>About this place</h3>${natureDetailRow('State',d.state)}${natureDetailRow('District',d.district)}${natureDetailRow('Locality',d.locality)}${natureDetailRow('Category',d.category)}${natureDetailRow('Famous for',d.knownFor)}${natureDetailRow('Description',d.description)}${natureDetailRow('Best season',d.bestSeason)}${natureDetailRow('Difficulty',d.difficulty)}</div><div class="panel"><h3>Location & verification</h3>${natureDetailRow('Latitude',d.latitude)}${natureDetailRow('Longitude',d.longitude)}${natureDetailRow('Distance','Click “Calculate distance”')}${natureDetailRow('Popularity',d.popularityTier)}${natureDetailRow('Primary source',d.primarySource)}${natureDetailRow('Verification',d.verificationStatus)}${natureDetailRow('Record status',d.recordStatus)}<div class="nature-distance-box"><strong id="nature-distance-value">Not calculated</strong><button class="btn btn-outline btn-sm" type="button" onclick="requestNatureDistance('${encodeURIComponent(d.locationId||'')}')">Calculate distance from my location</button></div></div></div><div id="nature-online-guide" class="online-guide panel"><span class="info-kicker">ONLINE DESTINATION GUIDE</span><h3>Source information</h3><p>Loading source information…</p></div><div class="spot-actions"><button class="btn btn-primary" onclick="openNatureResolvedMap('${encodeURIComponent(d.locationId||'')}')">Open Location in Google Maps</button><button class="btn btn-outline" onclick="openNatureResolvedDirections('${encodeURIComponent(d.locationId||'')}')">Get Directions From My Location</button></div><div id="nature-fare-box" class="destination-fare-box" hidden></div><div class="spot-actions"><button class="btn btn-outline" onclick="checkNatureFare('${encodeURIComponent(d.locationId||'')}')">₹ Calculate My Rent</button><button class="btn btn-primary" onclick="findTripToDestination('${esc(d.name)}')">🚐 Find Trips & Book</button></div><p class="form-hint">All visitors can view destination information without login. Verification labels are preserved from the registry and are not presented as government certification.</p></div></div>`);
  const name=d.name||'';const exactTitle=SPECIAL_WIKI_TITLES[photoCoreName(name)];const info=exactTitle?await fetchWikipediaExactTitle(exactTitle,name):await fetchWikipediaDestination(name);const special=specialDestinationPhoto(name);const photo=special?.url||info?.image||await fetchOnlinePhoto('NATURE',d.locationId,name);const photoEl=document.getElementById('nature-detail-photo');if(photoEl&&photo)photoEl.innerHTML=`<img src="${esc(photo)}" alt="${esc(name)}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">${esc(special?.credit||(info?.image?'Wikipedia':'Wikimedia Commons'))}</span>`;
  const guide=document.getElementById('nature-online-guide');if(guide&&info?.extract)guide.innerHTML=`<span class="info-kicker">ONLINE DESTINATION GUIDE</span><h3>Why ${esc(name)} is special</h3><p>${esc(info.extract)}</p>${info.url?`<a class="btn btn-outline btn-sm" href="${esc(info.url)}" target="_blank" rel="noopener noreferrer">Read source</a>`:''}`;
  if(!hasGps){
    const resolved=await resolveNatureCoordinates(d);
    const modal=document.getElementById('nature-modal');
    if(resolved&&modal){
      const lat2=resolved.lat,lng2=resolved.lng;
      const grid=modal.querySelector('.nature-detail-grid');
      const locPanel=grid?.querySelectorAll('.panel')?.[1];
      if(locPanel){locPanel.innerHTML=`<h3>Location & verification</h3>${natureDetailRow('Latitude',lat2.toFixed(6))}${natureDetailRow('Longitude',lng2.toFixed(6))}${natureDetailRow('Distance','Click “Calculate distance”')}${natureDetailRow('Popularity',d.popularityTier)}${natureDetailRow('Primary source',d.primarySource)}${natureDetailRow('Verification',d.verificationStatus)}${natureDetailRow('Record status',d.recordStatus)}<div class="nature-distance-box"><strong id="nature-distance-value">Not calculated</strong><button class="btn btn-outline btn-sm" type="button" onclick="requestNatureDistance('${encodeURIComponent(d.locationId||'')}')">Calculate distance from my location</button></div>`;}
      const actions=modal.querySelector('.spot-actions');if(actions)actions.outerHTML=`<div class="spot-actions"><button class="btn btn-primary" onclick="openNatureMap('${lat2}','${lng2}')">Open Location in Google Maps</button><button class="btn btn-outline" onclick="openNatureDirections('${lat2}','${lng2}')">Get Directions From My Location</button></div>`;
    }
  }
}
async function checkNatureFare(encodedId){const id=decodeURIComponent(encodedId||'');const d=NATURE_DESTINATIONS.find(x=>String(x.locationId)===id);if(!d)return;await renderDestinationFare('nature-fare-box',d,d.name||d.locality||'Destination',d.name||d.locality||'');}
async function openNatureResolvedMap(encodedId){const id=decodeURIComponent(encodedId||'');const d=NATURE_DESTINATIONS.find(x=>String(x.locationId)===id);if(d)await openResolvedDestinationMap(d);}
async function openNatureResolvedDirections(encodedId){const id=decodeURIComponent(encodedId||'');const d=NATURE_DESTINATIONS.find(x=>String(x.locationId)===id);if(d)await openResolvedDestinationDirections(d);}
function openNatureMap(lat,lng){if(validGeoPoint(lat,lng))window.open(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${lat},${lng}`)}`,'_blank','noopener');}

async function hydrateNaturePhotos(){const nodes=[...document.querySelectorAll('[data-nature-photo]')];let i=0;const worker=async()=>{while(i<nodes.length){const n=nodes[i++];const name=n.dataset.naturePhoto||'';const exactTitle=SPECIAL_WIKI_TITLES[photoCoreName(name)];const info=exactTitle?await fetchWikipediaExactTitle(exactTitle,name):await fetchWikipediaDestination(name);if(info?.image){n.innerHTML=`<img src="${esc(info.image)}" alt="${esc(name)}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">Wikipedia</span>`;}else{const url=await fetchOnlinePhoto('NATURE',n.dataset.natureId||'',name);if(url)n.innerHTML=`<img src="${esc(url)}" alt="${esc(name)}" loading="lazy" referrerpolicy="no-referrer"><span class="photo-credit">Wikimedia Commons</span>`;else n.innerHTML='<span>🌿</span><small>Photo pending verified match</small>';}}};await Promise.all([worker(),worker(),worker(),worker()]);}

// =====================================================
// COMMUNITY / SAFETY / SMART HUBS
// =====================================================
function hubPage(title, eyebrow, intro, cards){
  return `${header()}<main class="page hub-page"><section class="page-header hub-hero"><div class="container"><span class="eyebrow page-eyebrow">${eyebrow}</span><h1>${title}</h1><p class="section-sub">${intro}</p></div></section><section class="section"><div class="container"><div class="hub-grid">${cards.map(c=>`<article class="hub-card"><div class="hub-icon">${c.icon}</div><span class="tag">${c.tag}</span><h2>${c.title}</h2><p>${c.desc}</p><button class="btn btn-primary btn-sm" onclick="go('${c.route}')">${c.cta}</button></article>`).join('')}</div></div></section></main>${footer()}`;
}
function community(){return hubPage('Sahyadri Community','COMMUNITY','Connect with fellow trekkers, share current trail information and build a safer trekking community.',[
 {icon:'👥',tag:'TREK MATE',title:'Trek Mate Finder',desc:'Create a trek plan, discover nearby travellers and request to join compatible groups.',cta:'Find Trek Mates',route:'trek-mates'},
 {icon:'📸',tag:'TRAVELLER UPDATES',title:'Photos & Trail Conditions',desc:'Share recent photos and practical updates about roads, water, weather and trail conditions.',cta:'Explore Updates',route:'forts'},
 {icon:'🏆',tag:'GAMIFICATION',title:'Leaderboard & Badges',desc:'Earn progress badges as you explore forts and destinations. Your activity stays connected to your account.',cta:'View Leaderboard',route:'leaderboard'}
]);}
function trekMates(){return hubPage('Trek Mate Finder','COMMUNITY / TREK MATES','Plan a trek with people who are interested in the same destination, date and difficulty.',[
 {icon:'🗓️',tag:'PLAN',title:'Create a Trek Plan',desc:'Choose destination, date, difficulty, group size and meeting point. Backend matching can be enabled without changing this interface.',cta:'Create Plan',route:'login'},
 {icon:'🔎',tag:'DISCOVER',title:'Browse Trek Plans',desc:'See public plans and send a join request. Personal contact details remain private until the group rules allow sharing.',cta:'Browse Plans',route:'login'},
 {icon:'🛡️',tag:'SAFETY',title:'Community Safety',desc:'Use verified accounts, reporting and emergency tools. Trek Mate is a coordination feature, not a guarantee of personal safety.',cta:'Open Safety',route:'safety'}
]);}
function leaderboard(){return hubPage('Explorer Leaderboard','COMMUNITY / BADGES','A transparent activity-based layer for fort and trek exploration.',[
 {icon:'🥾',tag:'PROGRESS',title:'Sahyadri Explorer',desc:'Example milestone: 5 completed fort explorations.',cta:'View Progress',route:'profile'},
 {icon:'🏔️',tag:'MILESTONE',title:'Fort Conqueror',desc:'Example milestone: 15 completed fort explorations.',cta:'View Badges',route:'profile'},
 {icon:'📊',tag:'LEADERBOARD',title:'Community Ranking',desc:'Leaderboard will be based on recorded activity, not self-declared visits. Final scoring rules can be managed by admin.',cta:'Open Profile',route:'profile'}
]);}
function offlineTrails(){return hubPage('Offline Trails & GPX','SAFETY / OFFLINE MAPS','Download route files before leaving network coverage and keep essential trail information available offline.',[
 {icon:'🗺️',tag:'GPX',title:'Download Trail GPX',desc:'Fort route records can expose downloadable GPX files once a verified route file is attached to the route record.',cta:'Explore Fort Routes',route:'forts'},
 {icon:'📱',tag:'OFFLINE',title:'Offline Route Pack',desc:'The interface is prepared for cached route data, essential waypoints and emergency information on mobile.',cta:'Open Safety',route:'safety'},
 {icon:'⚠️',tag:'BEFORE YOU GO',title:'Download Before Departure',desc:'Network availability is not guaranteed in the hills. Keep a local copy of your route and emergency information.',cta:'Safety Checklist',route:'safety'}
]);}
function guides(){return hubPage('Local Guides & Experiences','LOCAL TOURISM','Discover verified base-village guides and local experiences around forts and destinations.',[
 {icon:'🧑‍🏫',tag:'GUIDES',title:'Local Guide Booking',desc:'Guide partners can publish availability, service area, language and pricing after verification.',cta:'Partner With Us',route:'partner'},
 {icon:'🏡',tag:'BASE VILLAGE',title:'Homestays & Local Food',desc:'Connect local stays and food experiences with the nearby fort or destination.',cta:'Find Stays',route:'stays'},
 {icon:'🤝',tag:'LOCAL PARTNERS',title:'Become a Local Partner',desc:'Apply as a guide or local experience provider. Admin review can be applied before publishing.',cta:'Partner With Us',route:'partner'}
]);}
function weather(){return hubPage('Live Weather','SMART / WEATHER','Weather cards are designed to sit directly on destination pages and can later consume a weather provider API.',[
 {icon:'🌦️',tag:'DESTINATION',title:'Fort Weather',desc:'Show current conditions and forecast near the selected fort when coordinates and provider data are available.',cta:'Explore Forts',route:'forts'},
 {icon:'🌧️',tag:'ALERTS',title:'Rain & Trail Alerts',desc:'Use weather conditions as a supporting signal for trek planning. Alerts should never replace official warnings.',cta:'Open Safety',route:'safety'},
 {icon:'📍',tag:'LOCATION',title:'Location-aware Forecast',desc:'Destination coordinates can power the weather request without exposing the traveller’s precise home location.',cta:'Explore Destinations',route:'spots'}
]);}
function aiRecommendations(){return hubPage('AI Trek Recommender','SMART TRAVEL','Get trek suggestions based on difficulty, duration, season and interests. The recommendation layer can use the fort registry and destination data already in the project.',[
 {icon:'🤖',tag:'RECOMMEND',title:'Tell us what you like',desc:'Choose Easy, Moderate or Hard; one-day or longer; monsoon, winter or all-season.',cta:'Start Recommendation',route:'login'},
 {icon:'🧭',tag:'MATCH',title:'Destination Matching',desc:'Match preferences against the fort and destination registry instead of inventing places.',cta:'Explore Forts',route:'forts'},
 {icon:'🌦️',tag:'SMART',title:'Weather-aware Suggestions',desc:'Future recommendations can combine current weather and forecast signals with route difficulty.',cta:'View Weather',route:'weather'}
]);}

// =====================================================
// ROUTER
// =====================================================
function render() {
  state.page = initialPage();
  const pages = { home, trips, forts, spots, how, stays, register, login, dashboard, "create-trip": createTrip, "agency-create-trip": agencyCreateTrip, "create-property": createProperty, return: returnPage, profile, safety, partner, contact, about, privacy, terms, refund, trust, community, "trek-mates": trekMates, leaderboard, "offline-trails": offlineTrails, guides, weather, "ai-recommendations": aiRecommendations, "forgot-password": forgotPassword, "agency-register": agencyRegister, "driver-register": driverRegister, "hotel-register": hotelRegister, complaint, "settings-password": settingsPassword, "india-nature": indiaNature, nature: indiaNature };
  app.innerHTML = (pages[state.page] || home)();
  applyLanguage();
  window.scrollTo(0, 0);

  if (state.page === "home") hydrateHomePhotos();
  if (state.page === "register") updateRegFields();
  if (state.page === "driver-register") loadVehicleCatalog().then(hydrateDriverVehicleOptions);
  if (state.user) syncPendingSOS();
  if (state.page === "trips") { loadVehicleCatalog().then(loadTrips); }
  if (state.page === "forts") loadForts();
  if (state.page === "spots") loadSpots();
  if (state.page === "india-nature" || state.page === "nature") loadNatureDestinations();
  if (state.page === "stays") loadProperties();
  if (state.page === "profile") loadProfile();
  if (state.page === "safety") loadSafetyContact();
  if (state.page === "create-trip" && state.user?.role === "DRIVER") loadDriverVehicleForTrip();
  if (state.page === "agency-create-trip" && state.user?.role === "AGENCY") hydrateAgencyCreateTrip();
  if (state.page === "dashboard" && state.user) {
    if (state.user.role === "DRIVER") loadDriverDashboard();
    else if (state.user.role === "AGENCY") { loadVehicleCatalog().then(loadAgencyDashboard); }
    else if (state.user.role === "HOTEL_OWNER") loadHotelDashboard();
    else if (state.user.role === "ADMIN") loadAdminDashboard();
    else loadTravelerDashboard();
  }
}

window.addEventListener("hashchange", render);
window.addEventListener("load", render);

document.addEventListener("DOMContentLoaded",()=>{setTimeout(applyLanguage,0);document.addEventListener("change",e=>{if(e.target?.id==="agv-type")updateAgencyVehicleCapacity();});});
