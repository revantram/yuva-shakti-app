// ==========================================
// 1. Firebase कॉन्फ़िगरेशन (आधिकारिक वेब ऐप)
// ==========================================
const firebaseConfig = {
  apiKey: "AIzaSyCPaqSP09VBYpcLgy2FiYWZ4-avH0K1f_8",
  authDomain: "yuva-shakti-group-ac131.firebaseapp.com",
  projectId: "yuva-shakti-group-ac131",
  storageBucket: "yuva-shakti-group-ac131.firebasestorage.app",
  messagingSenderId: "8889452055",
  appId: "1:8889452055:web:dae314c019eef1c525be52",
  measurementId: "G-NNE5T6MTM8",
  databaseURL: "https://yuva-shakti-group-ac131-default-rtdb.asia-southeast1.firebasedatabase.app"
};

// Firebase शुरू करें
let app = null;
let db = null;
try {
  if (typeof firebase !== "undefined" && firebase.initializeApp) {
    if (!firebase.apps.length) {
      app = firebase.initializeApp(firebaseConfig);
    } else {
      app = firebase.app();
    }
    db = firebase.firestore();
  }
} catch (e) {
  console.warn("Firebase Init Error:", e);
}

// ==========================================
// 2. मोबाइल OTP सत्यापन एवं ऐप अनलॉक सिस्टम
// ==========================================
let confirmationResultGlobal = null;

// Invisible reCAPTCHA सेटअप
function setupRecaptcha() {
  if (!window.recaptchaVerifier && typeof firebase !== 'undefined' && firebase.auth) {
    window.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptcha-container', {
      'size': 'invisible',
      'callback': function(response) {
        // reCAPTCHA पूर्ण हुआ
      }
    });
  }
}

// 1. OTP भेजने का लॉजिक
const sendOtpBtn = document.getElementById('sendOtpBtn');
if (sendOtpBtn) {
  sendOtpBtn.addEventListener('click', () => {
    const phone = document.getElementById('mobileNumberInput').value.trim();
    const errorEl = document.getElementById('lockError');
    errorEl.innerText = "";

    if (phone.length !== 10) {
      errorEl.innerText = "कृपया 10 अंकों का सही मोबाइल नंबर दर्ज करें!";
      return;
    }

    sendOtpBtn.disabled = true;
    sendOtpBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> OTP भेजा जा रहा है...';

    setupRecaptcha();
    const appVerifier = window.recaptchaVerifier;
    const phoneNumber = "+91" + phone;

    firebase.auth().signInWithPhoneNumber(phoneNumber, appVerifier)
      .then((confirmationResult) => {
        confirmationResultGlobal = confirmationResult;
        sendOtpBtn.disabled = false;
        sendOtpBtn.innerHTML = '<i class="fa-solid fa-paper-plane"></i> OTP भेजें';
        
        // OTP इनपुट दृश्य दिखाएं
        document.getElementById('phoneStep').style.display = 'none';
        document.getElementById('otpStep').style.display = 'block';
        document.getElementById('lockSubtitle').innerText = "मोबाइल पर आया OTP दर्ज करें";
      })
      .catch((error) => {
        sendOtpBtn.disabled = false;
        sendOtpBtn.innerHTML = '<i class="fa-solid fa-paper-plane"></i> OTP भेजें';
        errorEl.innerText = "त्रुटि: " + (error.message || "OTP नहीं भेजा जा सका");
        if (window.recaptchaVerifier) {
          window.recaptchaVerifier.render().then(widgetId => grecaptcha.reset(widgetId));
        }
      });
  });
}

// 2. OTP सत्यापित करने का लॉजिक
const verifyOtpBtn = document.getElementById('verifyOtpBtn');
if (verifyOtpBtn) {
  verifyOtpBtn.addEventListener('click', () => {
    const code = document.getElementById('otpCodeInput').value.trim();
    const errorEl = document.getElementById('lockError');
    const lockCard = document.querySelector('.lock-card');
    errorEl.innerText = "";

    if (code.length !== 6) {
      errorEl.innerText = "कृपया 6 अंकों का OTP दर्ज करें!";
      return;
    }

    if (!confirmationResultGlobal) {
      errorEl.innerText = "सत्र समाप्त हुआ। कृपया OTP दोबारा भेजें।";
      return;
    }

    verifyOtpBtn.disabled = true;
    verifyOtpBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> सत्यापन जारी है...';

    confirmationResultGlobal.confirm(code)
      .then((result) => {
        const phone = document.getElementById('mobileNumberInput').value.trim();
        
        // एडमिन नंबर होने पर एडमिन अधिकार दें
        if (phone === "8696663615" || phone === "992942") {
          document.body.classList.add('is-admin');
        }

        // ऐप अनलॉक करें
        document.getElementById('appLockModal').style.display = 'none';
        document.getElementById('mainApp').style.display = 'flex';
        initApp(); // मुख्य ऐप शुरू करें
      })
      .catch((error) => {
        verifyOtpBtn.disabled = false;
        verifyOtpBtn.innerHTML = '<i class="fa-solid fa-unlock"></i> OTP सत्यापित करें';
        errorEl.innerText = "गलत OTP कोड! कृपया पुनः प्रयास करें।";
        if (lockCard) {
          lockCard.classList.add('shake');
          setTimeout(() => lockCard.classList.remove('shake'), 400);
        }
      });
  });
}

// नंबर बदलने का बटन
const changePhoneBtn = document.getElementById('changePhoneBtn');
if (changePhoneBtn) {
  changePhoneBtn.addEventListener('click', () => {
    document.getElementById('otpStep').style.display = 'none';
    document.getElementById('phoneStep').style.display = 'block';
    document.getElementById('lockSubtitle').innerText = "मोबाइल OTP द्वारा सत्यापन";
    document.getElementById('lockError').innerText = "";
  });
}

// ==========================================
// 3. डार्क / लाइट मोड टॉगल
// ==========================================
const themeToggleBtn = document.getElementById('themeToggleBtn');
const darkThemeCheckbox = document.getElementById('darkThemeCheckbox');

function toggleTheme(isDark) {
  const theme = isDark ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('app-theme', theme);
  if (darkThemeCheckbox) darkThemeCheckbox.checked = isDark;
  if (themeToggleBtn) {
    themeToggleBtn.innerHTML = isDark ? '<i class="fa-solid fa-sun"></i>' : '<i class="fa-solid fa-moon"></i>';
  }
}

if (themeToggleBtn) {
  themeToggleBtn.addEventListener('click', () => {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    toggleTheme(currentTheme !== 'dark');
  });
}

if (darkThemeCheckbox) {
  darkThemeCheckbox.addEventListener('change', (e) => {
    toggleTheme(e.target.checked);
  });
}

// सहेजी गई थीम लोड करें
const savedTheme = localStorage.getItem('app-theme') || 'light';
toggleTheme(savedTheme === 'dark');

// ==========================================
// 4. दैनिक सुविचार
// ==========================================
const suvicharList = [
  "संगठन में ही शक्ति है, मिलकर आगे बढ़ें।",
  "सत्य और ईमानदारी ही सबसे बड़ा धर्म है।",
  "सेवा ही परमो धर्म है।",
  "एकता से हर मुश्किल काम आसान हो जाता है।"
];

function setDailyQuote() {
  const today = new Date().getDate();
  const quote = suvicharList[today % suvicharList.length];
  const quoteElem = document.getElementById('dailyQuote');
  if (quoteElem) quoteElem.innerText = quote;
}

// ==========================================
// 5. अगली अमावस्या लाइव काउंटडाउन
// ==========================================
// आगामी अमावस्या तिथियाँ (YYYY-MM-DD)
const amavasyaDates = [
  "2026-11-09T00:00:00",
  "2026-12-09T00:00:00",
  "2027-01-08T00:00:00"
];

function startCountdown() {
  const now = new Date().getTime();
  let nextAmavasya = null;

  for (let d of amavasyaDates) {
    const time = new Date(d).getTime();
    if (time > now) {
      nextAmavasya = new Date(d);
      break;
    }
  }

  if (!nextAmavasya) {
    const disp = document.getElementById('amavasyaDateDisplay');
    if (disp) disp.innerText = "अगली तिथि शीघ्र अपडेट होगी";
    return;
  }

  const options = { day: 'numeric', month: 'long', year: 'numeric' };
  const disp = document.getElementById('amavasyaDateDisplay');
  if (disp) disp.innerText = `तारीख: ${nextAmavasya.toLocaleDateString('hi-IN', options)}`;

  setInterval(() => {
    const currentTime = new Date().getTime();
    const diff = nextAmavasya.getTime() - currentTime;

    // WhatsApp Reminder check (2 Din Pehle Display)
    checkAmavasyaReminder(diff, nextAmavasya);

    if (diff <= 0) return;

    const d = Math.floor(diff / (1000 * 60 * 60 * 24));
    const h = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const s = Math.floor((diff % (1000 * 60)) / 1000);

    const dEl = document.getElementById('days');
    const hEl = document.getElementById('hours');
    const mEl = document.getElementById('minutes');
    const sEl = document.getElementById('seconds');

    if (dEl) dEl.innerText = String(d).padStart(2, '0');
    if (hEl) hEl.innerText = String(h).padStart(2, '0');
    if (mEl) mEl.innerText = String(m).padStart(2, '0');
    if (sEl) sEl.innerText = String(s).padStart(2, '0');
  }, 1000);
}

// ==========================================
// 6. iOS Liquid Dock Switching & Tactile Touch
// ==========================================
const dockBtns = document.querySelectorAll('.ios-dock-btn');
const tabPanes = document.querySelectorAll('.tab-pane');

dockBtns.forEach((btn) => {
  btn.addEventListener('click', () => {
    // Mobile Haptic Feedback (Vibration)
    if (navigator.vibrate) {
      navigator.vibrate(22);
    }

    // Active state update
    dockBtns.forEach((b) => b.classList.remove('active'));
    tabPanes.forEach((pane) => pane.classList.remove('active'));

    btn.classList.add('active');
    const targetPane = document.getElementById(btn.dataset.tab);
    if (targetPane) {
      targetPane.classList.add('active');
    }
  });
});

// ==========================================
// 7. हिंदू कैलेंडर (त्योहार व अमावस्या मार्कर)
// ==========================================
const festivals = {
  "2026-11-08": "दीपावली",
  "2026-11-09": "सोमवती अमावस्या",
  "2026-11-10": "गोवर्धन पूजा",
  "2026-11-11": "भाई दूज",
  "2026-12-09": "मार्गशीर्ष अमावस्या"
};

let currentCalDate = new Date();

function renderCalendar(date) {
  const grid = document.getElementById('calendarGrid');
  if (!grid) return;
  grid.innerHTML = "";

  const year = date.getFullYear();
  const month = date.getMonth();

  const monthNames = ["जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर"];
  const myEl = document.getElementById('calendarMonthYear');
  if (myEl) myEl.innerText = `${monthNames[month]} ${year}`;

  const days = ["रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि"];
  days.forEach(d => {
    const el = document.createElement('div');
    el.className = "cal-day-name";
    el.innerText = d;
    grid.appendChild(el);
  });

  const firstDay = new Date(year, month, 1).getDay();
  const totalDays = new Date(year, month + 1, 0).getDate();

  for (let i = 0; i < firstDay; i++) {
    const emptyCell = document.createElement('div');
    grid.appendChild(emptyCell);
  }

  const today = new Date();

  for (let day = 1; day <= totalDays; day++) {
    const cell = document.createElement('div');
    cell.className = "cal-cell";
    cell.innerText = day;

    if (
      today.getFullYear() === year &&
      today.getMonth() === month &&
      today.getDate() === day
    ) {
      cell.classList.add('today');
    }

    const formattedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

    // अमावस्या जांच
    if (festivals[formattedDate] && festivals[formattedDate].includes("अमावस्या")) {
      cell.classList.add("marker-amavasya");
    } else if (festivals[formattedDate]) {
      cell.classList.add("marker-festival");
    }

    cell.addEventListener('click', () => {
      const infoBox = document.getElementById('festivalInfoBox');
      if (!infoBox) return;
      if (festivals[formattedDate]) {
        infoBox.innerHTML = `<strong>${day} ${monthNames[month]}:</strong> ${festivals[formattedDate]}`;
      } else {
        infoBox.innerText = `${day} ${monthNames[month]}: कोई मुख्य त्योहार नहीं।`;
      }
    });

    grid.appendChild(cell);
  }
}

const prevMonthBtn = document.getElementById('prevMonthBtn');
if (prevMonthBtn) {
  prevMonthBtn.addEventListener('click', () => {
    currentCalDate.setMonth(currentCalDate.getMonth() - 1);
    renderCalendar(currentCalDate);
  });
}

const nextMonthBtn = document.getElementById('nextMonthBtn');
if (nextMonthBtn) {
  nextMonthBtn.addEventListener('click', () => {
    currentCalDate.setMonth(currentCalDate.getMonth() + 1);
    renderCalendar(currentCalDate);
  });
}

// ==========================================
// 8. Firebase रियल-टाइम सिंकिंग (सदस्य व बहीखाता)
// ==========================================
let allMembersData = [];
let currentFilter = "all";

function renderMembersUI(members) {
  const listEl = document.getElementById('membersDepositList');
  if (!listEl) return;
  listEl.innerHTML = "";

  const filtered = members.filter(m => {
    if (currentFilter === "paid") return m.paid === true;
    if (currentFilter === "unpaid") return !m.paid;
    return true;
  });

  if (filtered.length === 0) {
    listEl.innerHTML = '<li style="padding: 12px; text-align: center; color: var(--text-muted); font-size: 13px;">कोई प्रविष्टि नहीं।</li>';
    return;
  }

  filtered.forEach((data) => {
    const li = document.createElement('li');
    li.style.cssText = "display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color); font-size: 13px; cursor: pointer;";
    li.setAttribute('title', 'विवरण और रसीद देखने के लिए क्लिक करें');
    li.innerHTML = `
      <span>${data.name}</span>
      <span style="color: ${data.paid ? 'var(--success-color)' : 'var(--danger-color)'}">
        ${data.paid ? `₹ ${data.amount || 100} (जमा)` : 'बकाया'}
      </span>
    `;
    li.addEventListener('click', () => {
      openMemberHistory(data.id || data.name, data.name, data.amount, data.paid);
    });
    listEl.appendChild(li);
  });
}

let currentTotalDeposits = 0;
let currentTotalExpenses = 0;

function updateRemainingBalance() {
  const balance = currentTotalDeposits - currentTotalExpenses;
  const balanceEl = document.getElementById('remainingBalance');
  if (balanceEl) {
    balanceEl.innerText = `₹ ${balance}`;
    balanceEl.style.color = balance >= 0 ? 'var(--success-color)' : 'var(--danger-color)';
  }
}

function listenRealtimeData() {
  if (!db) {
    // डिफ़ॉल्ट डेमो डेटा यदि ऑफ़लाइन / बिना की के हो
    allMembersData = [
      { id: "demo-1", name: "रेवंत राम भाटी", amount: 100, paid: true },
      { id: "demo-2", name: "महेन्द्र सिंह भाटी", amount: 100, paid: true },
      { id: "demo-3", name: "सुरेश भाटी", amount: 100, paid: false },
      { id: "demo-4", name: "दिनेश भाटी", amount: 100, paid: true },
      { id: "demo-5", name: "ओमप्रकाश भाटी", amount: 100, paid: false },
      { id: "demo-6", name: "कैलाश भाटी", amount: 100, paid: true }
    ];
    let totalDeposit = allMembersData.reduce((acc, m) => m.paid ? acc + m.amount : acc, 0);
    const totEl = document.getElementById('totalDepositAmount');
    if (totEl) totEl.innerText = `₹ ${totalDeposit}`;
    renderMembersUI(allMembersData);
    currentTotalDeposits = totalDeposit;
    updateRemainingBalance();
    return;
  }

  // सदस्यों की लिस्ट और जमा राशि
  db.collection("members").onSnapshot((snapshot) => {
    let totalDeposit = 0;
    allMembersData = [];

    snapshot.forEach((doc) => {
      const data = doc.data();
      data.id = doc.id;
      if (data.paid) totalDeposit += (data.amount || 0);
      allMembersData.push(data);
    });

    renderMembersUI(allMembersData);
    const totEl = document.getElementById('totalDepositAmount');
    if (totEl) totEl.innerText = `₹ ${totalDeposit}`;
    currentTotalDeposits = totalDeposit;
    updateRemainingBalance();
  }, (err) => {
    console.warn("Firestore members listener:", err);
  });

  // बहीखाता खर्च रियल-टाइम लिस्ट
  db.collection("expenses").orderBy("date", "desc").onSnapshot((snapshot) => {
    const expenseListEl = document.getElementById('expenseList');
    if (!expenseListEl) return;
    expenseListEl.innerHTML = "";
    let totalExpense = 0;

    snapshot.forEach((doc) => {
      const data = doc.data();
      totalExpense += (data.amount || 0);

      const li = document.createElement('li');
      li.style.cssText = "display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color); font-size: 13px;";
      li.innerHTML = `
        <div>
          <strong>${data.title}</strong>
          <div style="font-size: 11px; color: var(--text-muted);">${data.date}</div>
        </div>
        <span style="color: var(--danger-color); font-weight: bold;">- ₹ ${data.amount}</span>
      `;
      expenseListEl.appendChild(li);
    });
    currentTotalExpenses = totalExpense;
    updateRemainingBalance();
  }, (err) => {
    console.warn("Firestore expenses listener:", err);
  });
}

// जमा कार्ड क्लिक करने पर पॉपअप खोलना
const totalDepositCard = document.getElementById('totalDepositCard');
if (totalDepositCard) {
  totalDepositCard.addEventListener('click', () => {
    document.getElementById('depositDetailsModal').classList.add('active');
  });
}

const closeDepositDetails = document.getElementById('closeDepositDetails');
if (closeDepositDetails) {
  closeDepositDetails.addEventListener('click', () => {
    document.getElementById('depositDetailsModal').classList.remove('active');
  });
}

// फ़िल्टर बटन (सभी, जमा, बकाया)
document.querySelectorAll('.filter-tabs .filter-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.filter-tabs .filter-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    currentFilter = btn.dataset.filter || "all";
    renderMembersUI(allMembersData);
  });
});

// PDF एक्सपोर्ट
const exportPdfBtn = document.getElementById('exportPdfBtn');
if (exportPdfBtn) {
  exportPdfBtn.addEventListener('click', () => {
    if (typeof html2pdf !== 'undefined') {
      const element = document.getElementById('tab-accounts');
      html2pdf().from(element).save('Yuva_Shakti_BahiKhata.pdf');
    } else {
      window.print();
    }
  });
}

// ==========================================
// 9. इनिशियलाइज़ेशन
// ==========================================
function initApp() {
  setDailyQuote();
  startCountdown();
  renderCalendar(currentCalDate);
  listenRealtimeData();
  listenLoanRequests();
  listenGallery();
  trackActivePresence(); // <-- Real-time live count shuru karein
}

// ==========================================
// Real-Time Active Users Counter System
// ==========================================
function trackActivePresence() {
  if (typeof firebase === 'undefined' || !firebase.database) return;
  const rtdb = firebase.database();
  
  // Har user ke browser ke liye ek unique session ID
  let sessionId = localStorage.getItem('app_session_id');
  if (!sessionId) {
    sessionId = 'user_' + Math.random().toString(36).substr(2, 9);
    localStorage.setItem('app_session_id', sessionId);
  }

  const userStatusRef = rtdb.ref('/online_status/' + sessionId);
  const connectedRef = rtdb.ref('.info/connected');

  // Firebase connection monitor karna
  connectedRef.on('value', (snapshot) => {
    if (snapshot.val() === false) {
      return;
    }

    // App band hone par server se record hatana
    userStatusRef.onDisconnect().remove().then(() => {
      // Online hone par record set karna
      userStatusRef.set({
        online: true,
        lastSeen: firebase.database.ServerValue.TIMESTAMP
      });
    }).catch(err => console.warn("Presence disconnect error:", err));
  });

  // Sabhi online members ki ginti live sunna
  const allOnlineRef = rtdb.ref('/online_status');
  allOnlineRef.on('value', (snapshot) => {
    const count = snapshot.numChildren() || 1;
    const countEl = document.getElementById('liveMembersCount');
    if (countEl) {
      countEl.innerText = count;
    }
  });
}

// ==========================================
// 10. सहायता ऋण: मॉडल ओपन / क्लोज
// ==========================================
const loanModal = document.getElementById('loanModal');
const openLoanBtn = document.getElementById('newLoanRequestBtn');
const closeLoanBtn = document.getElementById('closeLoanModal');

if (openLoanBtn && loanModal) {
  openLoanBtn.addEventListener('click', () => {
    loanModal.classList.add('active');
  });
}

if (closeLoanBtn && loanModal) {
  closeLoanBtn.addEventListener('click', () => {
    loanModal.classList.remove('active');
  });
}

// ==========================================
// सहायता ऋण: अनुरोध सबमिट करना (Firebase में लाइव)
// ==========================================
const submitLoanBtn = document.getElementById('submitLoanRequestBtn');
if (submitLoanBtn) {
  submitLoanBtn.addEventListener('click', () => {
    const name = document.getElementById('loanApplicant').value.trim();
    const amount = document.getElementById('loanAmount').value.trim();
    const reason = document.getElementById('loanReason').value.trim();
    const duration = document.getElementById('loanDuration').value.trim();

    if (!name || !amount || !reason || !duration) {
      alert("कृपया सभी विवरण भरें!");
      return;
    }

    if (!db) {
      alert("Firebase से कनेक्शन सक्रिय नहीं है!");
      return;
    }

    db.collection("loans").add({
      name: name,
      amount: Number(amount),
      reason: reason,
      duration: duration,
      approvals: 0,
      rejections: 0,
      status: "pending",
      timestamp: firebase.firestore.FieldValue.serverTimestamp()
    }).then(() => {
      alert("अनुरोध सफलता पूर्वक भेजा गया!");
      if (loanModal) loanModal.classList.remove('active');
      document.getElementById('loanApplicant').value = '';
      document.getElementById('loanAmount').value = '';
      document.getElementById('loanReason').value = '';
      document.getElementById('loanDuration').value = '';
    }).catch((err) => {
      alert("एरर: " + err.message);
    });
  });
}

// ==========================================
// सहायता ऋण: लाइव लिस्ट एवं सदस्यों की वोटिंग
// ==========================================
function listenLoanRequests() {
  const container = document.getElementById('loanRequestsContainer');
  if (!container || !db) return;
  
  db.collection("loans").orderBy("timestamp", "desc").onSnapshot((snapshot) => {
    container.innerHTML = "";
    
    if (snapshot.empty) {
      container.innerHTML = "<p style='color: var(--text-muted); font-size: 13px; text-align: center; padding: 12px;'>वर्तमान में कोई ऋण अनुरोध सक्रिय नहीं है।</p>";
      return;
    }

    snapshot.forEach((doc) => {
      const data = doc.data();
      const card = document.createElement('div');
      card.style.cssText = "background: var(--bg-color); border: 1px solid var(--border-color); border-radius: 8px; padding: 12px; margin-bottom: 10px;";

      card.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
          <strong>${data.name}</strong>
          <span style="color: var(--primary-color); font-weight: bold;">₹ ${data.amount}</span>
        </div>
        <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 4px;"><strong>कारण:</strong> ${data.reason}</div>
        <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 8px;"><strong>वापसी अवधि:</strong> ${data.duration}</div>
        
        <div style="display: flex; gap: 8px; align-items: center; margin-top: 8px;">
          <button class="btn" style="flex: 1; background: var(--success-color); color: #fff; font-size: 12px;" onclick="voteLoan('${doc.id}', 'approve')">
            <i class="fa-solid fa-thumbs-up"></i> सहमत (${data.approvals || 0})
          </button>
          <button class="btn" style="flex: 1; background: var(--danger-color); color: #fff; font-size: 12px;" onclick="voteLoan('${doc.id}', 'reject')">
            <i class="fa-solid fa-thumbs-down"></i> असहमत (${data.rejections || 0})
          </button>
        </div>
      `;
      container.appendChild(card);
    });
  }, (err) => {
    console.warn("Firestore loans listener:", err);
  });
}

// वोट रिकॉर्ड करने का फंक्शन
window.voteLoan = function(docId, type) {
  if (!db) {
    alert("Firebase कनेक्ट नहीं है!");
    return;
  }
  const docRef = db.collection("loans").doc(docId);
  const fieldToIncrement = type === 'approve' ? 'approvals' : 'rejections';

  docRef.update({
    [fieldToIncrement]: firebase.firestore.FieldValue.increment(1)
  }).then(() => {
    alert("आपकी राय दर्ज कर ली गई है!");
  }).catch((err) => {
    alert("त्रुटि: " + err.message);
  });
};

// ==========================================
// 11. गैलरी: मॉडल खोलना और बंद करना
// ==========================================
const galleryModal = document.getElementById('galleryModal');
const openGalleryBtn = document.getElementById('uploadMediaBtn');
const closeGalleryBtn = document.getElementById('closeGalleryModal');

if (openGalleryBtn && galleryModal) {
  openGalleryBtn.addEventListener('click', () => {
    galleryModal.classList.add('active');
  });
}

if (closeGalleryBtn && galleryModal) {
  closeGalleryBtn.addEventListener('click', () => {
    galleryModal.classList.remove('active');
  });
}

// ==========================================
// गैलरी: नया मीडिया सेव करना (Firestore में लाइव)
// ==========================================
const submitMediaBtn = document.getElementById('submitMediaBtn');
if (submitMediaBtn) {
  submitMediaBtn.addEventListener('click', () => {
    const title = document.getElementById('mediaTitle').value.trim();
    const url = document.getElementById('mediaUrl').value.trim();
    const desc = document.getElementById('mediaDesc').value.trim();

    if (!title || !url) {
      alert("कृपया शीर्षक और लिंक दोनों दर्ज करें!");
      return;
    }

    if (!db) {
      alert("Firebase से कनेक्शन सक्रिय नहीं है!");
      return;
    }

    db.collection("gallery").add({
      title: title,
      url: url,
      desc: desc || "कोई विवरण नहीं",
      createdAt: firebase.firestore.FieldValue.serverTimestamp()
    }).then(() => {
      alert("फोटो/लिंक गैलरी में सफलतापूर्वक जुड़ गया!");
      if (galleryModal) galleryModal.classList.remove('active');
      document.getElementById('mediaTitle').value = '';
      document.getElementById('mediaUrl').value = '';
      document.getElementById('mediaDesc').value = '';
    }).catch((err) => {
      alert("एरर: " + err.message);
    });
  });
}

// ==========================================
// गैलरी: रियल-टाइम डिस्प्ले एवं डाउनलोड लिंक
// ==========================================
function listenGallery() {
  const galleryGrid = document.getElementById('galleryGrid');
  if (!galleryGrid || !db) return;

  db.collection("gallery").orderBy("createdAt", "desc").onSnapshot((snapshot) => {
    galleryGrid.innerHTML = "";

    if (snapshot.empty) {
      galleryGrid.innerHTML = "<p style='color: var(--text-muted); font-size: 13px; grid-column: 1/-1; text-align: center;'>गैलरी में अभी कोई फोटो या लिंक उपलब्ध नहीं है।</p>";
      return;
    }

    snapshot.forEach((doc) => {
      const item = doc.data();
      const card = document.createElement('div');
      card.className = "gallery-card";

      // यदि इमेज डायरेक्ट लिंक नहीं है तो डिफ़ॉल्ट इमेज दिखाएँ
      const isImg = item.url.match(/\.(jpeg|jpg|gif|png|webp)($|\?)/i);
      const thumbnailSrc = isImg ? item.url : 'https://placehold.co/300x200/png?text=युवा+शक्ति+ग्रुप';

      card.innerHTML = `
        <img src="${thumbnailSrc}" alt="${item.title}" onerror="this.src='https://placehold.co/300x200/png?text=फोटो'">
        <div class="gallery-card-body">
          <strong title="${item.title}">${item.title}</strong>
          <p>${item.desc}</p>
          <a href="${item.url}" target="_blank" rel="noopener noreferrer" class="download-link-btn">
            <i class="fa-solid fa-download"></i> देखें / डाउनलोड
          </a>
        </div>
      `;
      galleryGrid.appendChild(card);
    });
  }, (err) => {
    console.warn("Firestore gallery listener:", err);
  });
}

// ==========================================
// एडमिन: सदस्य प्रबंधन मोडल कंट्रोल
// ==========================================
const membersManageModal = document.getElementById('membersManageModal');
const openMembersModalBtn = document.getElementById('manageMembersBtn');
const closeMembersModalBtn = document.getElementById('closeMembersModal');

if (openMembersModalBtn && membersManageModal) {
  openMembersModalBtn.addEventListener('click', () => {
    // एडमिन सुरक्षा जांच
    const enteredPin = prompt("एडमिन सुरक्षा पिन दर्ज करें:");
    if (enteredPin === APP_PIN || enteredPin === ADMIN_PIN || enteredPin === "992942") {
      membersManageModal.classList.add('active');
      loadAdminMembersList();
    } else {
      alert("अमान्य पिन! केवल एडमिन को अनुमति है।");
    }
  });
}

if (closeMembersModalBtn && membersManageModal) {
  closeMembersModalBtn.addEventListener('click', () => {
    membersManageModal.classList.remove('active');
  });
}

// नया सदस्य जोड़ना
const saveMemberBtn = document.getElementById('saveMemberBtn');
if (saveMemberBtn) {
  saveMemberBtn.addEventListener('click', () => {
    const name = document.getElementById('newMemberName').value.trim();
    const amount = document.getElementById('newMemberAmount').value.trim();
    const isPaid = document.getElementById('newMemberPaid').checked;

    if (!name || !amount) {
      alert("कृपया सदस्य का नाम और राशि दर्ज करें!");
      return;
    }

    if (!db) {
      alert("Firebase से कनेक्शन सक्रिय नहीं है!");
      return;
    }

    db.collection("members").add({
      name: name,
      amount: Number(amount),
      paid: isPaid,
      updatedAt: firebase.firestore.FieldValue.serverTimestamp()
    }).then(() => {
      alert(`${name} को सफलतापूर्वक जोड़ दिया गया!`);
      document.getElementById('newMemberName').value = '';
      document.getElementById('newMemberAmount').value = '';
      document.getElementById('newMemberPaid').checked = false;
    }).catch((err) => {
      alert("एरर: " + err.message);
    });
  });
}

// एडमिन लिस्ट लोड करना (डिलीट और स्टेटस टॉगल बटन के साथ)
function loadAdminMembersList() {
  const listEl = document.getElementById('adminMembersList');
  if (!listEl || !db) return;
  
  db.collection("members").onSnapshot((snapshot) => {
    listEl.innerHTML = "";
    if (snapshot.empty) {
      listEl.innerHTML = "<li style='font-size: 12px; color: var(--text-muted); text-align: center; padding: 8px;'>कोई सदस्य नहीं मिला।</li>";
      return;
    }

    snapshot.forEach((doc) => {
      const data = doc.data();
      const li = document.createElement('li');
      li.style.cssText = "display: flex; justify-content: space-between; align-items: center; padding: 8px; border-bottom: 1px solid var(--border-color); font-size: 13px;";

      li.innerHTML = `
        <div>
          <strong>${data.name}</strong> - ₹${data.amount || 100}
          <div style="font-size: 11px; color: ${data.paid ? 'var(--success-color)' : 'var(--danger-color)'};">
            ${data.paid ? 'जमा' : 'बकाया'}
          </div>
        </div>
        <div style="display: flex; gap: 6px;">
          <button class="btn" style="background: ${data.paid ? 'var(--secondary-color)' : 'var(--success-color)'}; color: #fff; padding: 4px 8px; font-size: 11px;" onclick="togglePaymentStatus('${doc.id}', ${data.paid})">
            ${data.paid ? 'बकाया करें' : 'जमा करें'}
          </button>
          <button class="btn" style="background: var(--danger-color); color: #fff; padding: 4px 8px; font-size: 11px;" onclick="confirmRemoveMember('${doc.id}', '${data.name}')">
            <i class="fa-solid fa-trash"></i>
          </button>
        </div>
      `;
      listEl.appendChild(li);
    });
  }, (err) => {
    console.warn("Firestore admin members listener:", err);
  });
}

// सदस्य की जमा स्थिति बदलना
window.togglePaymentStatus = function(docId, currentStatus) {
  if (!db) return;
  db.collection("members").doc(docId).update({
    paid: !currentStatus
  });
};

// सदस्य हटाने की पुष्टि
window.confirmRemoveMember = function(docId, name) {
  if (!db) return;
  if (confirm(`क्या आप सचमुच ${name} को ग्रुप से हटाना चाहते हैं?`)) {
    db.collection("members").doc(docId).delete()
      .then(() => alert(`${name} को सफलतापूर्वक हटा दिया गया!`))
      .catch((err) => alert("हटाने में विफल: " + err.message));
  }
};

// ==========================================
// एडमिन: खर्च प्रबंधन मोडल कंट्रोल
// ==========================================
const expenseModal = document.getElementById('expenseModal');
const openExpenseBtn = document.getElementById('addExpenseBtn');
const closeExpenseBtn = document.getElementById('closeExpenseModal');

if (openExpenseBtn && expenseModal) {
  openExpenseBtn.addEventListener('click', () => {
    const enteredPin = prompt("एडमिन सुरक्षा पिन दर्ज करें:");
    if (enteredPin === APP_PIN || enteredPin === ADMIN_PIN || enteredPin === "992942") {
      expenseModal.classList.add('active');
    } else {
      alert("अमान्य पिन!");
    }
  });
}

if (closeExpenseBtn && expenseModal) {
  closeExpenseBtn.addEventListener('click', () => {
    expenseModal.classList.remove('active');
  });
}

// नया खर्च सहेजना
const saveExpenseBtn = document.getElementById('saveExpenseBtn');
if (saveExpenseBtn) {
  saveExpenseBtn.addEventListener('click', () => {
    const title = document.getElementById('expenseTitle').value.trim();
    const amount = document.getElementById('expenseAmount').value.trim();

    if (!title || !amount) {
      alert("कृपया खर्च का नाम और राशि भरें!");
      return;
    }

    if (!db) {
      alert("Firebase से कनेक्शन सक्रिय नहीं है!");
      return;
    }

    const today = new Date();
    const formattedDate = `${today.getDate()}/${today.getMonth() + 1}/${today.getFullYear()}`;

    db.collection("expenses").add({
      title: title,
      amount: Number(amount),
      date: formattedDate,
      createdAt: firebase.firestore.FieldValue.serverTimestamp()
    }).then(() => {
      alert("खर्च सफलतापूर्वक दर्ज हो गया!");
      if (expenseModal) expenseModal.classList.remove('active');
      document.getElementById('expenseTitle').value = '';
      document.getElementById('expenseAmount').value = '';
    }).catch((err) => {
      alert("एरर: " + err.message);
    });
  });
}

// ==========================================
// UPI पेमेंट एवं डायनामिक QR कोड सिस्टम
// ==========================================
const GROUP_UPI_ID = "revantbhati95@okaxis"; // <-- यहाँ ग्रुप या एडमिन की UPI ID लिखें
const GROUP_NAME = "Yuva Shakti Group";

const upiModal = document.getElementById('upiPaymentModal');
const openUpiBtn = document.getElementById('openUpiBtn');
const closeUpiBtn = document.getElementById('closeUpiModal');
const upiAmountInput = document.getElementById('upiAmountInput');
const upiQrCodeImg = document.getElementById('upiQrCodeImg');
const directUpiPayBtn = document.getElementById('directUpiPayBtn');

function updateUpiDetails() {
  const amount = (upiAmountInput && upiAmountInput.value) ? upiAmountInput.value : "500";
  const note = encodeURIComponent("Yuva Shakti Amavasya Contribution");
  const upiUrl = `upi://pay?pa=${GROUP_UPI_ID}&pn=${encodeURIComponent(GROUP_NAME)}&am=${amount}&cu=INR&tn=${note}`;

  // सीधे UPI ऐप से पेमेंट का लिंक सेट करें
  if (directUpiPayBtn) {
    directUpiPayBtn.setAttribute('href', upiUrl);
  }

  // फ्री QR API से लाइव QR कोड इमेज जेनरेट करें
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(upiUrl)}`;
  if (upiQrCodeImg) {
    upiQrCodeImg.src = qrApiUrl;
  }
}

if (openUpiBtn && upiModal) {
  openUpiBtn.addEventListener('click', () => {
    updateUpiDetails();
    upiModal.classList.add('active');
  });
}

if (closeUpiBtn && upiModal) {
  closeUpiBtn.addEventListener('click', () => {
    upiModal.classList.remove('active');
  });
}

// राशि बदलते ही QR कोड और लिंक तुरंत अपडेट हो जाएगा
if (upiAmountInput) {
  upiAmountInput.addEventListener('input', updateUpiDetails);
}

// ==========================================
// WhatsApp Auto Reminder Logic (2 Din Pehle Display)
// ==========================================
const reminderBox = document.getElementById('whatsappReminderBox');
const sendAlertBtn = document.getElementById('sendWhatsAppAlertBtn');

let nextAmavasyaTargetDate = null;

// Countdown logic ke saath date track karna
function checkAmavasyaReminder(diffInMs, targetDate) {
  nextAmavasyaTargetDate = targetDate;
  const twoDaysInMs = 2 * 24 * 60 * 60 * 1000;

  // Agar bacha hua samay 2 din (48 ghante) ya usse kam ho aur samay bita na ho
  if (diffInMs <= twoDaysInMs && diffInMs > 0) {
    if (reminderBox) reminderBox.style.display = 'block';
  } else {
    // Agar 2 din se zyada samay hai toh button hide rahega
    if (reminderBox) reminderBox.style.display = 'none';
  }
}

// Button click hone par WhatsApp redirect
if (sendAlertBtn) {
  sendAlertBtn.addEventListener('click', () => {
    let dateStr = "आगामी अमावस्या";
    if (nextAmavasyaTargetDate) {
      const options = { day: 'numeric', month: 'long', year: 'numeric' };
      dateStr = nextAmavasyaTargetDate.toLocaleDateString('hi-IN', options);
    }

    const message = 
      `*युवा शक्ति ग्रुप (भाटी परिवार)* 🚩\n\n` +
      `प्रिय सदस्य,\n` +
      `आगामी अमावस्या (${dateStr}) को हमारा मासिक अंशदान जमा करने का समय नजदीक आ गया है।\n\n` +
      `कृपया सभी सदस्य समय पर अपना अंशदान जमा करवाकर सहयोग करें।\n\n` +
      `_धन्यवाद,_ \n` +
      `*रेवंत राम भाटी* (व्यवस्थापक)`;

    const encodedMsg = encodeURIComponent(message);
    const whatsappUrl = `https://api.whatsapp.com/send?text=${encodedMsg}`;
    
    window.open(whatsappUrl, '_blank');
  });
}

// ==========================================
// Sadasya History & Digital Receipt System
// ==========================================
const historyModal = document.getElementById('memberHistoryModal');
const closeHistoryBtn = document.getElementById('closeHistoryModal');
let currentReceiptData = null;

if (closeHistoryBtn) {
  closeHistoryBtn.addEventListener('click', () => {
    if (historyModal) historyModal.classList.remove('active');
  });
}

// Sadasya par click hone par history aur receipt open karne ka function
window.openMemberHistory = function(docId, memberName, amount, isPaid) {
  const hName = document.getElementById('historyMemberName');
  if (hName) hName.innerText = `${memberName} - विवरण`;
  const rName = document.getElementById('receiptMemberName');
  if (rName) rName.innerText = memberName;
  const rAmount = document.getElementById('receiptAmount');
  if (rAmount) rAmount.innerText = `₹ ${amount || 500}`;
  
  const todayFormatted = new Date().toLocaleDateString('hi-IN', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  });
  const rDate = document.getElementById('receiptDate');
  if (rDate) rDate.innerText = todayFormatted;

  currentReceiptData = {
    name: memberName,
    amount: amount || 500,
    date: todayFormatted,
    status: isPaid ? "जमा (Paid)" : "बकाया (Pending)"
  };

  const receiptCard = document.getElementById('digitalReceiptCard');
  if (receiptCard) {
    if (isPaid) {
      receiptCard.style.display = 'block';
    } else {
      // Agar baki hai toh green receipt card chupa dein
      receiptCard.style.display = 'none';
    }
  }

  // Pichhle 6 mahino ka record banana
  loadPastMonthsRecords(docId, isPaid, amount);
  if (historyModal) historyModal.classList.add('active');
};

// Past records generate & load
function loadPastMonthsRecords(memberId, currentMonthPaid, amount) {
  const monthsListEl = document.getElementById('memberMonthsList');
  if (!monthsListEl) return;
  monthsListEl.innerHTML = "";

  const monthNames = ["जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर"];
  const current = new Date();

  for (let i = 0; i < 6; i++) {
    const d = new Date(current.getFullYear(), current.getMonth() - i, 1);
    const mName = monthNames[d.getMonth()] + " " + d.getFullYear();
    const paidStatus = (i === 0) ? currentMonthPaid : true; // Pichhle mahine paid demo format

    const li = document.createElement('li');
    li.className = "history-item";
    li.innerHTML = `
      <span><i class="fa-regular fa-calendar-check" style="margin-right: 5px;"></i> ${mName}</span>
      <span style="font-weight: bold; color: ${paidStatus ? 'var(--success-color)' : 'var(--danger-color)'};">
        ${paidStatus ? `₹ ${amount || 500} (जमा)` : 'बकाया'}
      </span>
    `;
    monthsListEl.appendChild(li);
  }
}

// Digital Receipt WhatsApp Share Button Trigger
const shareReceiptBtn = document.getElementById('shareReceiptWhatsAppBtn');
if (shareReceiptBtn) {
  shareReceiptBtn.addEventListener('click', () => {
    if (!currentReceiptData) return;

    const msg = 
      `*अंशदान डिजिटल रसीद (Payment Slip)* 🧾\n` +
      `*युवा शक्ति ग्रुप (भाटी परिवार)* 🚩\n` +
      `-----------------------------------\n` +
      `👤 *सदस्य का नाम:* ${currentReceiptData.name}\n` +
      `💰 *जमा राशि:* ₹ ${currentReceiptData.amount}\n` +
      `📅 *दिनांक:* ${currentReceiptData.date}\n` +
      `✅ *स्थिति:* ${currentReceiptData.status}\n` +
      `-----------------------------------\n` +
      `_यह रसीद युवा शक्ति ग्रुप सिस्टम द्वारा जनरेट की गई है।_\n` +
      `व्यवस्थापक: *रेवंत राम भाटी*`;

    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(msg)}`;
    window.open(url, '_blank');
  });
}
// ================= वंशावली (Family Tree) डेटा व लॉजिक =================

// वंशावली का संपूर्ण डेटा (पीढ़ी 2 से पीढ़ी 6)
const vanshavaliData = [
    {
        id: "head-1",
        name: "बुधाराम जी",
        spouseName: "राधा देवी",
        gender: "male",
        generation: 2,
        children: [
            {
                id: "gen3-1",
                name: "केवल राम जी भाटी",
                spouseName: "कमली देवी",
                gender: "male",
                generation: 3,
                parentId: "head-1",
                children: [
                    {
                        id: "gen4-1",
                        name: "रूपाराम जी भाटी",
                        spouseName: "शारदा देवी",
                        gender: "male",
                        generation: 4,
                        parentId: "gen3-1",
                        children: [
                            {
                                id: "gen5-1",
                                name: "रेवंतराम भाटी",
                                spouseName: "पुष्पा देवी",
                                gender: "male",
                                generation: 5,
                                parentId: "gen4-1",
                                children: [
                                    {
                                        id: "gen6-1",
                                        name: "ऋतिक भाटी",
                                        spouseName: "",
                                        gender: "male",
                                        generation: 6,
                                        parentId: "gen5-1",
                                        children: []
                                    }
                                ]
                            },
                            {
                                id: "gen5-2",
                                name: "सुभाष भाटी",
                                spouseName: "",
                                gender: "male",
                                generation: 5,
                                parentId: "gen4-1",
                                children: []
                            }
                        ]
                    },
                    {
                        id: "gen4-2",
                        name: "शौकीन राम जी भाटी",
                        spouseName: "गलकू देवी",
                        gender: "male",
                        generation: 4,
                        parentId: "gen3-1",
                        children: []
                    },
                    {
                        id: "gen4-3",
                        name: "मोतीराम जी भाटी",
                        spouseName: "साबू देवी",
                        gender: "male",
                        generation: 4,
                        parentId: "gen3-1",
                        children: []
                    },
                    {
                        id: "gen4-4",
                        name: "संजय भाटी",
                        spouseName: "निरमा (संगीता)",
                        gender: "male",
                        generation: 4,
                        parentId: "gen3-1",
                        children: []
                    },
                    {
                        id: "gen4-5",
                        name: "सुमन",
                        spouseName: "",
                        gender: "female",
                        generation: 4,
                        parentId: "gen3-1",
                        children: []
                    }
                ]
            },
            {
                id: "gen3-2",
                name: "केसाराम जी",
                spouseName: "सांपत देवी",
                gender: "male",
                generation: 3,
                parentId: "head-1",
                children: []
            }
        ]
    },
    {
        id: "head-2",
        name: "रामू राम जी",
        spouseName: "—",
        gender: "male",
        generation: 2,
        children: []
    },
    {
        id: "head-3",
        name: "देबू जी",
        spouseName: "—",
        gender: "male",
        generation: 2,
        children: []
    },
    {
        id: "head-4",
        name: "नेनाराम जी",
        spouseName: "—",
        gender: "male",
        generation: 2,
        children: []
    },
    {
        id: "head-5",
        name: "राजूराम जी",
        spouseName: "—",
        gender: "male",
        generation: 2,
        children: []
    }
];

let vanshavaliStack = [];

// सदस्य आईडी से खोजना
function findVanshavaliMember(id, list = vanshavaliData) {
    for (let m of list) {
        if (m.id === id) return m;
        if (m.children && m.children.length > 0) {
            let found = findVanshavaliMember(id, m.children);
            if (found) return found;
        }
    }
    return null;
}

// वंशावली रेंडर करना
function renderVanshavaliView() {
    const breadcrumb = document.getElementById("vanshavali-breadcrumb");
    const hazariDiagram = document.getElementById("hazari-diagram");
    const parentHeader = document.getElementById("branch-parent-header");
    const listTitle = document.getElementById("branch-list-title");
    const cardsList = document.getElementById("vanshavali-cards-list");

    if (!cardsList) return;
    cardsList.innerHTML = "";

    // ब्रेडक्रम्ब अपडेट
    if (breadcrumb) {
        breadcrumb.innerHTML = `<span class="breadcrumb-item ${vanshavaliStack.length === 0 ? 'active' : ''}" onclick="resetVanshavali()">मूल वृक्ष</span>`;
        let path = [];
        vanshavaliStack.forEach((id, idx) => {
            path.push(id);
            const m = findVanshavaliMember(id);
            const isLast = idx === vanshavaliStack.length - 1;
            const targetPath = [...path];
            breadcrumb.innerHTML += ` <span>&gt;</span> <span class="breadcrumb-item ${isLast ? 'active' : ''}" onclick="jumpVanshavali(['${targetPath.join("','")}'])">${m ? m.name : 'शाखा'}</span>`;
        });
    }

    if (vanshavaliStack.length === 0) {
        // रूट व्यू (हजारी जी का स्थिर डायग्राम + 5 परिवार)
        if (hazariDiagram) hazariDiagram.style.display = "block";
        if (parentHeader) parentHeader.style.display = "none";
        if (listTitle) listTitle.innerText = "5 मुख्य परिवार (पीढ़ी 2)";

        vanshavaliData.forEach(head => {
            cardsList.appendChild(createVanshavaliCard(head));
        });
    } else {
        // ड्रिलडाउन शाखा दृश्य
        if (hazariDiagram) hazariDiagram.style.display = "none";
        const currentId = vanshavaliStack[vanshavaliStack.length - 1];
        const parentMember = findVanshavaliMember(currentId);

        if (!parentMember) {
            resetVanshavali();
            return;
        }

        if (parentHeader) {
            parentHeader.style.display = "block";
            parentHeader.innerHTML = `
                <div style="background: #eff6ff; border-left: 4px solid #2563eb; padding: 10px 14px; border-radius: 8px;">
                    <div style="font-size: 11px; font-weight: bold; color: #1d4ed8;">वर्तमान परिवार शाखा (पीढ़ी ${parentMember.generation})</div>
                    <div style="font-size: 15px; font-weight: bold; color: #1e3a8a; margin-top: 2px;">
                        [ ${parentMember.name} + ${parentMember.spouseName || 'धर्मपत्नी'} ]
                    </div>
                </div>
            `;
        }

        if (listTitle) listTitle.innerText = `संतानें (पीढ़ी ${parentMember.generation + 1})`;

        if (!parentMember.children || parentMember.children.length === 0) {
            cardsList.innerHTML = `<div style="text-align: center; color: #64748b; padding: 25px 0; font-size: 13px;">इस शाखा में अभी आगे कोई सदस्य दर्ज नहीं है।</div>`;
        } else {
            parentMember.children.forEach(child => {
                if (child.gender === "female") {
                    cardsList.appendChild(createDaughterVanshavaliCard(child));
                } else {
                    cardsList.appendChild(createVanshavaliCard(child));
                }
            });
        }
    }
}

// दंपति कार्ड [ पति + पत्नी ]
function createVanshavaliCard(member) {
    const card = document.createElement("div");
    card.className = "couple-bracket-card";
    const childrenCount = (member.children || []).length;

    card.innerHTML = `
        <div class="couple-header">
            <span class="gen-badge">पीढ़ी ${member.generation}</span>
            <span style="font-size: 12px; font-weight: bold; color: #2563eb;">${childrenCount} संतानें (खोलें) &gt;</span>
        </div>
        <div class="couple-row">
            <div class="member-cell">
                <i class="fa-solid fa-circle-user" style="font-size: 32px; color: #64748b;"></i>
                <div>
                    <div style="font-size: 10px; font-weight: bold; color: #d97706;">पति</div>
                    <div style="font-size: 14px; font-weight: bold; color: #1e293b;">${member.name}</div>
                </div>
            </div>
            <div class="plus-sign">+</div>
            <div class="member-cell" style="text-align: right; flex-direction: row-reverse;">
                <i class="fa-solid fa-circle-user" style="font-size: 32px; color: #db2777;"></i>
                <div>
                    <div style="font-size: 10px; font-weight: bold; color: #db2777;">पत्नी</div>
                    <div style="font-size: 14px; font-weight: bold; color: #1e293b;">${member.spouseName || '—'}</div>
                </div>
            </div>
        </div>
    `;

    card.onclick = () => {
        vanshavaliStack.push(member.id);
        renderVanshavaliView();
    };

    return card;
}

// पुत्री कार्ड
function createDaughterVanshavaliCard(member) {
    const card = document.createElement("div");
    card.style.cssText = "background: #fff1f2; border: 1.5px solid #fecdd3; border-radius: 12px; padding: 10px 14px; margin-bottom: 10px; display: flex; align-items: center; gap: 10px;";
    card.innerHTML = `
        <i class="fa-solid fa-circle-user" style="font-size: 28px; color: #e11d48;"></i>
        <div>
            <div style="font-size: 10px; font-weight: bold; color: #e11d48;">पुत्री (पीढ़ी ${member.generation})</div>
            <div style="font-size: 14px; font-weight: bold; color: #881337;">${member.name}</div>
        </div>
    `;
    return card;
}

function resetVanshavali() {
    vanshavaliStack = [];
    renderVanshavaliView();
}

function jumpVanshavali(pathArray) {
    vanshavaliStack = pathArray;
    renderVanshavaliView();
}

// बॉटम डॉक बटन से जोड़ना
document.addEventListener("DOMContentLoaded", () => {
    const dockButtons = document.querySelectorAll(".ios-dock-btn");
    dockButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const targetTab = btn.getAttribute("data-tab");
            if (targetTab === "tab-vanshavali") {
                document.querySelectorAll(".tab-pane").forEach(pane => pane.style.display = "none");
                const vSection = document.getElementById("tab-vanshavali");
                if (vSection) vSection.style.display = "block";

                dockButtons.forEach(b => b.classList.remove("active"));
                btn.classList.add("active");

                renderVanshavaliView();
            }
        });
    });
});

