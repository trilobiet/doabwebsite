/* main.js
 * OAPEN UI interactions (navbar, loaders, utilities)
 * Includes legacy code from scripts.js
 */

'use strict';

document.addEventListener('DOMContentLoaded', () => {
  // ===== Helpers ============================================================

  /**
   * Shorthand querySelectorAll into an array.
   * @param {string} sel - CSS selector
   * @param {ParentNode|Document} [root=document]
   * @returns {HTMLElement[]}
   */
  const $all = (sel, root = document) => Array.prototype.slice.call(root.querySelectorAll(sel), 0);

  /**
   * Safe innerHTML load via fetch with basic error swallow.
   * Accepts "url [space] selector" to extract a fragment.
   * @param {HTMLElement} el - target element
   * @param {string} urlWithOptionalSelector - "URL" or "URL CSS_SELECTOR"
   * @returns {Promise<void>}
   */
  async function loadHTML(el, urlWithOptionalSelector) {
    try {
      const raw = (urlWithOptionalSelector || '').trim();
      if (!raw) return;

      // Split "url selector" (first space separates them)
      const spaceIdx = raw.indexOf(' ');
      const url = (spaceIdx === -1 ? raw : raw.slice(0, spaceIdx)).trim();
      const selector = spaceIdx === -1 ? '' : raw.slice(spaceIdx + 1).trim();

      const res = await fetch(url, { credentials: 'same-origin' });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const text = await res.text();

      if (!selector) {
        el.innerHTML = text;
        return;
      }

      // Extract only the requested fragment(s)
      const doc = new DOMParser().parseFromString(text, 'text/html');
      const nodes = Array.from(doc.querySelectorAll(selector));
      el.innerHTML = nodes.map(n => n.outerHTML).join('') || '';
    } catch (e) {
      // whatever
    }
  }

  // ===== Navbar: burger + expand/condense on scroll =========================

  /**
   * Wire up Bulma burger: toggles 'bulma-is-active' on burger and its target,
   * and maintains aria-expanded.
   */
  (function initBurger() {
    // Get all "navbar-burger" elements
    const burgers = $all('.bulma-navbar-burger');
    burgers.forEach((el) => {
      el.addEventListener('click', (evt) => {
        evt.preventDefault();
        // Get the target from the "data-target" attribute
        const targetId = el.dataset.target || 'oa-nav';
        const target = document.getElementById(targetId) || document.getElementById('oa-nav');
        if (!target) return;

        // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
        const active = target.classList.toggle('bulma-is-active');
        el.classList.toggle('bulma-is-active', active);
        el.setAttribute('aria-expanded', active ? 'true' : 'false');
      });
    });
  })();

  /**
   * Navbar expand/condense on scroll
   */
  (function initHeaderScroll() {
    const header = document.getElementById('oa-header');
    if (!header) return;

    // expand when we scroll back near top; condense only after a bit more scroll
    const EXPAND_AT   = 12;
    const CONDENSE_AT = 56; 

    let isCondensed = header.classList.contains('js-is-condensed');
    let raf = 0;

    // apply expanded state (at the top)
    function expand() {
      if (!isCondensed) return;
      header.classList.remove('js-is-condensed');
      isCondensed = false;
    }

    // apply condensed state (after scrolling down)
    function condense() {
      if (isCondensed) return;
      header.classList.add('js-is-condensed');
      isCondensed = true;
    }

    // read scroll position and apply hysteresis thresholds.
    function tick() {
      raf = 0;
      const y = Math.max(0, window.scrollY || 0);

      if (!isCondensed && y > CONDENSE_AT) {
        condense();
      } else if (isCondensed && y < EXPAND_AT) {
        expand();
      }
    }

    // scroll handler
    function onScroll() {
      if (raf) return;
      raf = requestAnimationFrame(tick);
    }

    // initial state (also apply immediately so first paint is correct)
    if (Math.max(0, window.scrollY || 0) > CONDENSE_AT) {
      header.classList.add('js-is-condensed');
      isCondensed = true;
    } else {
      header.classList.remove('js-is-condensed');
      isCondensed = false;
    }

    window.addEventListener('scroll', onScroll, { passive: true });
  })();

  // ===== Newsletter modal ===================================================
  (function initNewsletterModal() {
    const modal    = document.getElementById('newsletter-modal');
    const triggers = $all('.js-open-modal[data-modal="newsletter-modal"]');
    if (!modal || !triggers.length) return;

    const ACTIVE_CLASSES = ['bulma-is-active', 'is-active'];

    // Keep a list of top-level siblings so we can flip aria-hidden
    const bodyChildren = Array.from(document.body.children);

    let lastFocused = null;

    function setActive(on) {
      ACTIVE_CLASSES.forEach(c => modal.classList.toggle(c, on));
    }

    function lockScroll(on) {
      const html = document.documentElement;
      const body = document.body;
      if (on) {
        html.__prevOverflow = html.style.overflow;
        body.__prevOverflow = body.style.overflow;
        html.style.overflow = 'hidden';
        body.style.overflow = 'hidden';
      } else {
        html.style.overflow = html.__prevOverflow || '';
        body.style.overflow = body.__prevOverflow || '';
      }
    }

    // Hide non-modal content from screen readers
    function setBackgroundAriaHidden(on) {
      bodyChildren.forEach(el => {
        if (el === modal || el.contains(modal)) return; // skip the modal and any ancestor
        if (on) {
          if (!el.hasAttribute('aria-hidden')) el.__wasAriaHidden = null;
          else el.__wasAriaHidden = el.getAttribute('aria-hidden');
          el.setAttribute('aria-hidden', 'true');
        } else {
          if (el.__wasAriaHidden === null) el.removeAttribute('aria-hidden');
          else if (el.__wasAriaHidden !== undefined) el.setAttribute('aria-hidden', el.__wasAriaHidden);
          delete el.__wasAriaHidden;
        }
      });
    }

    function focusables(root) {
      return $all(
        'a[href], area[href], input:not([disabled]), select:not([disabled]), ' +
        'textarea:not([disabled]), button:not([disabled]), iframe, object, embed, ' +
        '[contenteditable], [tabindex]:not([tabindex="-1"])',
        root
      ).filter(el => (el.offsetParent !== null) || el === root);
    }

    function trapTab(e) {
      if (e.key !== 'Tab') return;
      const f = focusables(modal);
      if (!f.length) return;
      const first = f[0], last = f[f.length - 1];
      if (e.shiftKey && document.activeElement === first) {
        e.preventDefault(); last.focus();
      } else if (!e.shiftKey && document.activeElement === last) {
        e.preventDefault(); first.focus();
      }
    }

    function onEsc(e) {
      if (e.key === 'Escape') close();
    }

    function open(triggerEl) {
      lastFocused = triggerEl || document.activeElement;

      setActive(true);
      lockScroll(true);
      setBackgroundAriaHidden(true);

      // focus into the dialog
      modal.setAttribute('tabindex', '-1');
      modal.focus();
      const first = focusables(modal)[0];
      if (first) first.focus();

      // reflect expanded state for a11y on the triggering control(s)
      triggers.forEach(t => t.setAttribute('aria-expanded', 'true'));

      modal.addEventListener('keydown', trapTab);
      document.addEventListener('keydown', onEsc);
    }

    function close() {
      setActive(false);
      lockScroll(false);
      setBackgroundAriaHidden(false);

      modal.removeEventListener('keydown', trapTab);
      document.removeEventListener('keydown', onEsc);

      triggers.forEach(t => t.setAttribute('aria-expanded', 'false'));

      if (lastFocused && typeof lastFocused.focus === 'function') {
        lastFocused.focus();
      }
    }

    // Open handlers (support multiple triggers)
    triggers.forEach((btn) => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        open(btn);
      });
    });

    // Close handlers (x and backdrop)
    modal.addEventListener('click', (e) => {
      if (
        e.target.classList.contains('bulma-modal-background') ||
        e.target.closest('.js-close-modal')
      ) {
        e.preventDefault();
        close();
      }
    });
  })();

  // ===== Section headers w/ random decorative stars =========================
  (function initSectionHeaderStars() {
    const els = $all('.oa-section-header');
    if (!els.length) return;

    const STAR_SVGS = [
      '/static-assets/svg/Star1.svg',
      '/static-assets/svg/Star2.svg',
      '/static-assets/svg/Star3.svg',
      '/static-assets/svg/Star4.svg',
    ];
    const SIZES = [16, 18, 24, 28, 32];

    const rand = (min, max) => min + Math.random() * (max - min);
    const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];
    const jitterPx = (px) => (Math.random() * 2 - 1) * px;

    const Y_UP = 3; // px star can go up
    const Y_DOWN = 1; // px star can go down
    const jitterY = () => Math.round((Math.random() * Y_DOWN) - Y_UP);

    const MIN_MARGIN = 6;  // px: extra gap to avoid overlap

    els.forEach((el) => {
      // --- Star #1
      const url1  = pick(STAR_SVGS);
      const size1 = pick(SIZES);
      const x1Pct = Math.round(rand(8, 92));
      const y1    = `${jitterY()}px`;
      const rot1  = `${Math.round(jitterPx(18))}deg`;
      const op1   = (0.85 + Math.random() * 0.15).toFixed(2);

      // --- Star #2 (70% chance)
      const show2 = Math.random() < 0.7;
      let url2 = 'none', size2 = 0, x2Pct = 50, y2 = '0px', rot2 = '0deg', op2 = '0';

      if (show2) {
        url2  = pick(STAR_SVGS.filter(u => u !== url1));
        size2 = pick(SIZES.filter(s => s !== size1));
        y2    = `${jitterY()}px`;
        rot2  = `${Math.round(jitterPx(18))}deg`;
        op2   = (0.85 + Math.random() * 0.15).toFixed(2);

        const w = Math.max(1, el.clientWidth || 1); // avoid div by 0
        const minDxPx = (size1 + size2) / 2 + MIN_MARGIN;
        const minDxPct = (minDxPx / w) * 100;

        let attempts = 0;
        do {
          x2Pct = Math.round(rand(18, 88));
          attempts++;
        } while (Math.abs(x2Pct - x1Pct) < minDxPct && attempts < 12);
        if (Math.abs(x2Pct - x1Pct) < minDxPct) {
          x2Pct = (x1Pct < 50) ? Math.min(95, x1Pct + minDxPct) : Math.max(5, x1Pct - minDxPct);
        }
      }

      // Apply CSS vars (percent positions as strings with %)
      el.style.setProperty('--star-url', `url(${url1})`);
      el.style.setProperty('--star-size', `${size1}px`);
      el.style.setProperty('--star-x', `${x1Pct}%`);
      el.style.setProperty('--star-y-offset', y1);
      el.style.setProperty('--star-rotate', rot1);
      el.style.setProperty('--star-opacity', op1);

      el.style.setProperty('--star2-url', `url(${url2})`);
      el.style.setProperty('--star2-size', `${size2}px`);
      el.style.setProperty('--star2-x', `${x2Pct}%`);
      el.style.setProperty('--star2-y-offset', y2);
      el.style.setProperty('--star2-rotate', rot2);
      el.style.setProperty('--star2-opacity', op2);
    });
  })();

  // ===== Legacy behaviours (from scripts.js) ================================

  // make iframes full height
  (function autoHeightIframes() {
    $all('.oapen-snippet iframe').forEach((ifr) => {
      ifr.addEventListener('load', () => {
        try {
          const doc = ifr.contentDocument || ifr.contentWindow?.document;
          if (doc && doc.body) {
            ifr.style.height = doc.body.scrollHeight + 'px';
          }
        } catch (e) {
          // Cross-origin: cannot measure; ignore.
        }
      });
    });
  })();

  // ajaxloader
  (function initAjaxLoader() {
    $all('.ajaxloader').forEach((el) => {
      const src = el.getAttribute('data-src');
      if (!src) return;
      el.innerHTML = " <i class='fa fa-spinner fa-pulse fa-fw'></i>\
        <i>Connecting to library&hellip;</i>";
      loadHTML(el, src);
    });
  })();

  // ===== Featured Titles: Splide carousel =========
  (function initFeaturedTitles() {
    function mount(scope = document) {
      const opts = {
        type: 'loop',
        perPage: 6,
        gap: '1.5rem',
        arrows: true,
        pagination: false,
        drag: true,
        autoplay: true,
        interval: 7000,
        speed: 1200,
        easing: 'cubic-bezier(0.22, 1, 0.36, 1)',
        pauseOnHover: true,
        pauseOnFocus: true,
        wheel: false,
        breakpoints: {
          1280: { perPage: 5 },
          1024: { perPage: 4 },
          768:  { perPage: 3 },
          480:  { perPage: 2 },
        },
      };

      const roots = $all('.splide', scope);
      roots.forEach((root) => {
        if (root.__splide) return;
        root.__splide = new Splide(root, opts).mount();
      });
    }

    // SSR case
    mount(document);

    // Async loader case
    $all('.carouselloader').forEach(async (el) => {
      const src = el.getAttribute('data-src');
      if (!src) return;
      el.innerHTML = " <i class='fa fa-spinner fa-pulse fa-fw'></i><i>Connecting to library&hellip;</i>";
      await loadHTML(el, src);
      mount(el);
    });
  })();


  // wrap content tables in a horizontal scrolling div
  (function wrapTables() {
    const tables = $all('.content table');
    tables.forEach((tbl) => {
      if (tbl.closest('.oapen-table-wrapper')) return;
      const wrapper = document.createElement('div');
      wrapper.className = 'oapen-table-wrapper';
      tbl.parentNode.insertBefore(wrapper, tbl);
      wrapper.appendChild(tbl);

      const hint = document.createElement('div');
      hint.className = 'oapen-table-swipe';
      hint.textContent = 'swipe to view table';
      wrapper.parentNode.insertBefore(hint, wrapper);
    });
  })();

  // faqs
  (function initFaqs() {
    $all('.oapen-foldout h3').forEach((h3) => {
      h3.addEventListener('click', () => {
        h3.classList.toggle('expanded');
      });
    });
  })();
});