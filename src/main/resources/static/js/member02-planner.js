/* Éclat | Member 02 - wedding planning workspace behaviour (vanilla JS, no dependencies) */
(function () {
  'use strict';

  // ---- Confirm before dangerous form submits -------------------------------------------
  document.querySelectorAll('[data-m02-confirm]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var message = form.getAttribute('data-m02-confirm') || 'Are you sure?';
      var taskName = form.getAttribute('data-m02-task-name');
      if (taskName) message = 'Delete "' + taskName + '" permanently?\nThis action cannot be undone.';
      if (!confirm(message)) e.preventDefault();
    });
  });

  // ---- Auto-submit selects (RSVP quick change) -----------------------------------------
  document.querySelectorAll('select[data-m02-autosubmit]').forEach(function (select) {
    select.addEventListener('change', function () { select.form.submit(); });
  });

  // ---- Print button ----------------------------------------------------------------------
  document.querySelectorAll('[data-m02-print]').forEach(function (b) {
    b.addEventListener('click', function () { window.print(); });
  });

  // ---- Generic edit modal ---------------------------------------------------------------
  // A button with data-m02-modal-open="<modalId>" and data-f-<fieldname>="value" attributes
  // opens the modal and copies each value into the form field whose name matches.
  function closeModal(modal) { modal.hidden = true; }
  document.querySelectorAll('.m02-modal-bg').forEach(function (modal) {
    modal.querySelectorAll('[data-m02-close]').forEach(function (x) {
      x.addEventListener('click', function () { closeModal(modal); });
    });
    modal.addEventListener('click', function (e) { if (e.target === modal) closeModal(modal); });
  });
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') document.querySelectorAll('.m02-modal-bg').forEach(closeModal);
  });
  document.querySelectorAll('[data-m02-modal-open]').forEach(function (button) {
    button.addEventListener('click', function () {
      var modal = document.getElementById(button.getAttribute('data-m02-modal-open'));
      if (!modal) return;
      var form = modal.querySelector('form');
      Array.prototype.forEach.call(button.attributes, function (attr) {
        if (attr.name.indexOf('data-f-') !== 0) return;
        var wanted = attr.name.substring(7);
        Array.prototype.forEach.call(form.elements, function (el) {
          if (el.name && el.name.toLowerCase() === wanted) el.value = attr.value;
        });
      });
      modal.hidden = false;
      var first = modal.querySelector('input[type=text],input[type=date],input[type=number]');
      if (first) first.focus();
    });
  });

  // ---- Filter tabs + search box over a list of items ---------------------------------------
  // <div data-m02-filterbar data-target=".selector"> with buttons [data-filter="STATUS|ALL"]
  // and an optional <input data-m02-q>. Items carry data-status and are searched by text.
  document.querySelectorAll('[data-m02-filterbar]').forEach(function (bar) {
    var items = document.querySelectorAll(bar.getAttribute('data-target'));
    var buttons = bar.querySelectorAll('[data-filter]');
    var box = bar.querySelector('[data-m02-q]');
    var empty = document.querySelector(bar.getAttribute('data-empty') || '#none');
    var current = 'ALL';
    function apply() {
      var q = box ? box.value.trim().toLowerCase() : '';
      var shown = 0;
      items.forEach(function (item) {
        var reminder = item.getAttribute('data-reminder') || 'NONE';
        var okStatus = current === 'ALL' ||
          (current === 'REMINDERS' ? reminder !== 'NONE' : item.getAttribute('data-status') === current);
        var okText = !q || item.textContent.toLowerCase().indexOf(q) !== -1;
        item.hidden = !(okStatus && okText);
        if (!item.hidden) shown++;
      });
      if (empty) empty.hidden = shown !== 0;
    }
    buttons.forEach(function (b) {
      b.addEventListener('click', function () {
        current = b.getAttribute('data-filter');
        buttons.forEach(function (x) { x.classList.toggle('active', x === b); });
        apply();
      });
    });
    if (box) box.addEventListener('input', apply);
  });

  // ---- Donut charts (SVG, no library) ---------------------------------------------------
  // <div data-m02-donut data-values="1,2,3" data-labels="A|B|C" data-colors="#aaa,#bbb,#ccc"
  //      data-center="12" data-center-label="tasks">
  var NS = 'http://www.w3.org/2000/svg';
  function svgEl(name, attrs) {
    var el = document.createElementNS(NS, name);
    Object.keys(attrs || {}).forEach(function (k) { el.setAttribute(k, attrs[k]); });
    return el;
  }
  document.querySelectorAll('[data-m02-donut]').forEach(function (host) {
    var values = (host.getAttribute('data-values') || '').split(',').map(function (v) { return parseFloat(v) || 0; });
    var labels = (host.getAttribute('data-labels') || '').split('|');
    var colors = (host.getAttribute('data-colors') || '').split(',');
    var total = values.reduce(function (a, b) { return a + b; }, 0);

    var svg = svgEl('svg', { viewBox: '0 0 42 42', role: 'img', 'aria-label': host.getAttribute('data-aria') || 'Chart' });
    svg.appendChild(svgEl('circle', { cx: 21, cy: 21, r: 15.9155, fill: 'none', stroke: '#f3e6e8', 'stroke-width': 6 }));
    var offset = 25; // start at 12 o'clock
    if (total > 0) {
      values.forEach(function (v, i) {
        if (v <= 0) return;
        var pct = v * 100 / total;
        svg.appendChild(svgEl('circle', {
          cx: 21, cy: 21, r: 15.9155, fill: 'none', stroke: colors[i] || '#999', 'stroke-width': 6,
          'stroke-dasharray': pct + ' ' + (100 - pct), 'stroke-dashoffset': offset
        }));
        offset -= pct;
      });
    }
    var num = svgEl('text', { x: 21, y: 22.5, 'text-anchor': 'middle', 'class': 'center-num', 'font-size': 7 });
    num.textContent = host.getAttribute('data-center') || (total > 0 ? String(Math.round(total)) : '0');
    svg.appendChild(num);
    var sub = svgEl('text', { x: 21, y: 27, 'text-anchor': 'middle', 'class': 'center-sub' });
    sub.textContent = host.getAttribute('data-center-label') || '';
    svg.appendChild(sub);

    var legend = document.createElement('ul');
    legend.className = 'legend';
    values.forEach(function (v, i) {
      var li = document.createElement('li');
      var dot = document.createElement('span');
      dot.className = 'dot';
      dot.style.background = colors[i] || '#999';
      var name = document.createElement('span');
      name.textContent = labels[i] || '';
      var val = document.createElement('span');
      val.className = 'val';
      val.textContent = host.getAttribute('data-money') === 'true' ? Math.round(v).toLocaleString('en-US') : String(v);
      li.appendChild(dot); li.appendChild(name); li.appendChild(val);
      legend.appendChild(li);
    });
    if (total === 0) {
      var none = document.createElement('li');
      none.textContent = host.getAttribute('data-none') || 'No data yet';
      legend.innerHTML = '';
      legend.appendChild(none);
    }
    host.classList.add('m02-chart');
    host.appendChild(svg);
    host.appendChild(legend);
  });

  // ---- Member 02: live, local wedding-style inspiration preview --------------------------
  // WeddingType codes are exactly the values in Java WeddingType.java and form radio buttons.
  // These photos are bundled locally (no CDN/network dependency).
  var stylePreview = document.getElementById('m02TypePreview');
  if (stylePreview) {
    var previewImage = document.getElementById('m02PreviewImage');
    var previewEyebrow = document.getElementById('m02PreviewEyebrow');
    var previewTitle = document.getElementById('m02PreviewTitle');
    var previewText = document.getElementById('m02PreviewText');
    var imageBase = stylePreview.getAttribute('data-image-base') || '/images/member02/';
    var choices = {
      STANDARD: {
        file: 'wedding-classic.jpg',
        label: 'CLASSIC / CUSTOM WEDDING',
        title: 'Classic elegance.',
        text: 'A timeless celebration with beautiful floral details and personal touches.',
        alt: 'Classic wedding ceremony with elegant floral arch'
      },
      BEACH: {
        file: 'wedding-beach.jpg',
        label: 'BEACH WEDDING',
        title: 'Sun, sea & celebration.',
        text: 'A romantic ceremony by the ocean, filled with warm light and relaxed charm.',
        alt: 'Beach wedding ceremony under a floral arch at sunset'
      },
      TRADITIONAL: {
        file: 'wedding-traditional.jpg',
        label: 'TRADITIONAL PORUWA WEDDING',
        title: 'Tradition with meaning.',
        text: 'Honor Sri Lankan wedding customs with a beautiful, meaningful Poruwa ceremony.',
        alt: 'Traditional Sri Lankan wedding ceremony with ornate floral decorations'
      },
      GARDEN: {
        file: 'wedding-garden.jpg',
        label: 'GARDEN WEDDING',
        title: 'Blooming garden romance.',
        text: 'An elegant open-air celebration surrounded by flowers and lush greenery.',
        alt: 'Outdoor garden wedding ceremony decorated with flowers'
      },
      HOTEL: {
        file: 'wedding-ballroom.jpg',
        label: 'HOTEL BALLROOM WEDDING',
        title: 'Grand ballroom glamour.',
        text: 'A refined indoor celebration with golden details and an elegant reception.',
        alt: 'Luxury hotel ballroom prepared for a wedding reception'
      }
    };
    var latestPreviewRequest = 0;

    function setPreviewForType(type) {
      var data = choices[type] || choices.STANDARD;
      previewEyebrow.textContent = data.label;
      previewTitle.textContent = data.title;
      previewText.textContent = data.text;
      var imageUrl = imageBase + data.file;
      // Preload to avoid flashing blank images on slower computers.
      var requestNumber = ++latestPreviewRequest;
      if (previewImage.getAttribute('src') === imageUrl) {
        previewImage.alt = data.alt;
        return;
      }
      var preload = new Image();
      preload.onload = function () {
        if (requestNumber !== latestPreviewRequest) return;
        previewImage.src = imageUrl;
        previewImage.alt = data.alt;
        stylePreview.classList.remove('m02-preview-image-error');
      };
      preload.onerror = function () {
        if (requestNumber !== latestPreviewRequest) return;
        stylePreview.classList.add('m02-preview-image-error');
      };
      preload.src = imageUrl;
    }

    document.querySelectorAll('input[name="planType"]').forEach(function (radio) {
      radio.addEventListener('change', function () {
        if (radio.checked) setPreviewForType(radio.value);
      });
    });
    var selected = document.querySelector('input[name="planType"]:checked');
    setPreviewForType(selected ? selected.value : 'STANDARD');
  }

  // ---- Live estimate on the plan form -----------------------------------------------------
  // Asks the server (which calls the polymorphic calculateEstimatedCost of the chosen type).
  var box = document.getElementById('m02Estimate');
  if (box) {
    var url = box.getAttribute('data-url');
    var guests = document.getElementById('m02Guests');
    var budget = document.getElementById('m02Budget');
    var radios = document.querySelectorAll('input[name=planType]');
    var timer = null;
    var last = null;

    function selectedType() {
      var checked = document.querySelector('input[name=planType]:checked');
      return checked ? checked.value : 'STANDARD';
    }
    function render() {
      if (!last) return;
      box.querySelector('[data-est-label]').textContent = last.label;
      box.querySelector('[data-est-amount]').textContent = 'LKR ' + Number(last.estimate).toLocaleString('en-US');
      box.querySelector('[data-est-tasks]').textContent = last.tasks + ' recommended checklist tasks';
      box.querySelector('[data-est-advice]').textContent = last.advice;
      var note = box.querySelector('[data-est-compare]');
      var entered = parseFloat(budget && budget.value);
      if (!entered || entered <= 0) { note.textContent = ''; return; }
      var ratio = last.estimate / entered;
      note.className = 'mt-2 m02-warn' + (ratio > 1 ? ' bad' : '');
      note.textContent = ratio > 1 ? 'Your budget is below this estimate.'
        : (ratio > 0.9 ? 'Your budget covers the estimate, but it is tight.' : 'Your budget comfortably covers this estimate.');
    }
    function refresh() {
      var g = guests ? guests.value : '0';
      fetch(url + '?type=' + encodeURIComponent(selectedType()) + '&guests=' + encodeURIComponent(g || '0'),
        { credentials: 'same-origin' })
        .then(function (r) { return r.json(); })
        .then(function (data) { if (data && data.label) { last = data; render(); } })
        .catch(function () { /* the estimate is optional; ignore network errors */ });
    }
    function later() { clearTimeout(timer); timer = setTimeout(refresh, 250); }
    radios.forEach(function (r) { r.addEventListener('change', refresh); });
    if (guests) guests.addEventListener('input', later);
    if (budget) budget.addEventListener('input', render);
    refresh();
  }
})();
