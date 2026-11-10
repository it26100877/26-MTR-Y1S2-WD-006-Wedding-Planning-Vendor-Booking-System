<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Éclat | Discover Your Perfect Wedding</title>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Playfair+Display:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-royal.css?v=20261105-marketplace">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-marketplace.css?v=20261105">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-ui-2026.css?v=20261110-3">

<style id="eclat-vendor-picker-final-override">
  /* Isolated marketplace dropdown; deliberately loaded after every stylesheet. */
  #vendorPickerTrigger {
    display:flex !important; align-items:center !important; justify-content:space-between !important;
    width:100% !important; min-height:28px !important; margin:0 !important; padding:0 !important;
    border:0 !important; border-radius:0 !important; background:transparent !important;
    color:#77736d !important; box-shadow:none !important; text-align:left !important;
    font:400 .95rem 'DM Sans',sans-serif !important; line-height:1.4 !important;
  }
  #vendorPickerTrigger:hover,#vendorPickerTrigger:focus,#vendorPickerTrigger:active {
    background:transparent !important; color:#39352f !important; box-shadow:none !important; outline:0 !important;
  }
  #vendorPickerTrigger > span {font:inherit !important; color:inherit !important; letter-spacing:normal !important}
  .market-search .vendor-picker {position:relative !important; display:flex !important; flex-direction:column !important;
    justify-content:center !important; min-height:80px !important; padding:12px 24px !important;
    background:#fff !important; border-right:1px solid #eee7de !important; gap:7px !important; z-index:6 !important}
  .market-search .vendor-picker > span {display:block !important; color:#9c896d !important;
    font:700 .62rem 'DM Sans',sans-serif !important; letter-spacing:.16em !important; line-height:1.2 !important}
  #vendorPickerMenu {
    position:absolute !important; top:calc(100% - 1px) !important; left:0 !important; right:0 !important;
    z-index:9999 !important; display:grid !important; grid-template-columns:repeat(2,minmax(0,1fr)) !important;
    gap:3px !important; max-height:330px !important; overflow-y:auto !important; padding:12px !important;
    border:1px solid #eee5d9 !important; background:#fff !important; box-shadow:0 18px 40px #211a1426 !important;
  }
  #vendorPickerMenu[hidden] {display:none !important}
  #vendorPickerMenu .vendor-option {
    display:flex !important; align-items:center !important; justify-content:flex-start !important; gap:9px !important;
    width:100% !important; min-height:40px !important; margin:0 !important; padding:8px 10px !important;
    border:0 !important; border-radius:0 !important; background:#fff !important; color:#514b43 !important;
    box-shadow:none !important; text-align:left !important; font:400 .84rem 'DM Sans',sans-serif !important;
    line-height:1.25 !important; white-space:normal !important;
  }
  #vendorPickerMenu .vendor-option:hover,#vendorPickerMenu .vendor-option:focus,#vendorPickerMenu .vendor-option.selected {
    background:#f6f1e9 !important; color:#80653e !important; box-shadow:inset 2px 0 #b99a63 !important; outline:0 !important;
  }
  #vendorPickerMenu .vendor-option i {display:inline-flex !important; width:18px !important; flex:0 0 18px !important;
    justify-content:center !important; color:#9b8056 !important; font-size:.95rem !important}
  #vendorPickerMenu .vendor-option span {display:inline !important; color:inherit !important; font:inherit !important; letter-spacing:normal !important}
  @media(max-width:760px) {
    .market-search .vendor-picker {min-height:76px !important; padding:12px 18px !important;
      border-right:0 !important; border-bottom:1px solid #eee7de !important}
    #vendorPickerMenu {grid-template-columns:repeat(2,minmax(0,1fr)) !important; max-height:280px !important}
  }
</style>

</head>
<body class="marketplace-body">
<header class="market-nav">
  <a class="market-brand" href="${pageContext.request.contextPath}/">Éclat<span>.</span></a>
  <button class="market-menu-toggle" type="button" aria-label="Toggle navigation" aria-controls="marketLinks" aria-expanded="false"><i class="bi bi-list"></i></button>
  <nav class="market-links" id="marketLinks">
    <div class="dropdown">
      <a href="${pageContext.request.contextPath}/vendors" class="dropdown-toggle" data-bs-toggle="dropdown">Vendors</a>
      <div class="dropdown-menu market-dropdown">
        <c:forEach var="cat" items="${categories}"><a class="dropdown-item" href="${pageContext.request.contextPath}/vendors?category=${cat.name()}">${cat.displayName}</a></c:forEach>
        <a class="dropdown-item" href="${pageContext.request.contextPath}/vendors">All Vendors</a>
      </div>
    </div>
    <a href="${pageContext.request.contextPath}/vendors?category=VENUES">Venues</a>
    <a href="#destinations">Destinations</a><a href="#inspiration">Inspiration</a>
    <a href="#real-weddings">Real Weddings</a>
    <a href="#stories">Stories</a>
  </nav>
  <div class="market-account">
    <a href="${pageContext.request.contextPath}/register" class="market-signup">Sign Up</a>
    <a href="${pageContext.request.contextPath}/login" class="market-login">Log In</a>
  </div>
</header>

<main>
<section class="market-hero">
  <div class="market-hero-shade"></div>
  <div class="market-hero-content">
    <p class="eyebrow light">THE ART OF A BEAUTIFUL BEGINNING</p>
    <h1>Find the people<br>behind your <em>perfect day.</em></h1>
    <p class="hero-subtitle">Discover exceptional wedding professionals and bring your celebration to life.</p>
    <form class="market-search" action="${pageContext.request.contextPath}/vendors" method="get">
      <label class="location-field"><span>LOCATION</span><select name="location" aria-label="Select Sri Lankan district">
        <option value="">Sri Lanka · All districts</option>
        <option value="Ampara">Sri Lanka · Ampara</option><option value="Anuradhapura">Sri Lanka · Anuradhapura</option>
        <option value="Badulla">Sri Lanka · Badulla</option><option value="Batticaloa">Sri Lanka · Batticaloa</option>
        <option value="Colombo">Sri Lanka · Colombo</option><option value="Galle">Sri Lanka · Galle</option>
        <option value="Gampaha">Sri Lanka · Gampaha</option><option value="Hambantota">Sri Lanka · Hambantota</option>
        <option value="Jaffna">Sri Lanka · Jaffna</option><option value="Kalutara">Sri Lanka · Kalutara</option>
        <option value="Kandy">Sri Lanka · Kandy</option><option value="Kegalle">Sri Lanka · Kegalle</option>
        <option value="Kilinochchi">Sri Lanka · Kilinochchi</option><option value="Kurunegala">Sri Lanka · Kurunegala</option>
        <option value="Mannar">Sri Lanka · Mannar</option><option value="Matale">Sri Lanka · Matale</option>
        <option value="Matara">Sri Lanka · Matara</option><option value="Monaragala">Sri Lanka · Monaragala</option>
        <option value="Mullaitivu">Sri Lanka · Mullaitivu</option><option value="Nuwara Eliya">Sri Lanka · Nuwara Eliya</option>
        <option value="Polonnaruwa">Sri Lanka · Polonnaruwa</option><option value="Puttalam">Sri Lanka · Puttalam</option>
        <option value="Ratnapura">Sri Lanka · Ratnapura</option><option value="Trincomalee">Sri Lanka · Trincomalee</option>
        <option value="Vavuniya">Sri Lanka · Vavuniya</option>
      </select></label>
      <div class="vendor-search-field vendor-picker">
        <span>VENDOR</span>
        <input type="hidden" name="category" id="selectedVendorCategory" value="">
        <button type="button" class="vendor-picker-trigger" id="vendorPickerTrigger"
                aria-expanded="false" aria-controls="vendorPickerMenu">
          <span id="vendorPickerLabel">Choose a vendor category</span>
          <i class="bi bi-chevron-down"></i>
        </button>
        <input type="text" name="query" aria-label="Search vendor name" placeholder="Search vendor name..." autocomplete="off">
        <div class="vendor-picker-menu" id="vendorPickerMenu" hidden>
          <button type="button" class="vendor-option" data-category=""><i class="bi bi-grid"></i><span>All categories</span></button>
          <button type="button" class="vendor-option" data-category="PHOTOGRAPHY"><i class="bi bi-camera"></i><span>Photography</span></button>
          <button type="button" class="vendor-option" data-category="VIDEOGRAPHY"><i class="bi bi-camera-video"></i><span>Videography</span></button>
          <button type="button" class="vendor-option" data-category="CATERING"><i class="bi bi-cup-hot"></i><span>Catering</span></button>
          <button type="button" class="vendor-option" data-category="DECORATION"><i class="bi bi-flower1"></i><span>Decoration</span></button>
          <button type="button" class="vendor-option" data-category="MUSIC_DJ"><i class="bi bi-music-note-beamed"></i><span>Music / DJ</span></button>
          <button type="button" class="vendor-option" data-category="WEDDING_DRESSES"><i class="bi bi-gem"></i><span>Wedding Dresses</span></button>
          <button type="button" class="vendor-option" data-category="WEDDING_CAKES"><i class="bi bi-cake2"></i><span>Wedding Cakes</span></button>
          <button type="button" class="vendor-option" data-category="BEAUTY_SALON"><i class="bi bi-brush"></i><span>Beauty Salon</span></button>
          <button type="button" class="vendor-option" data-category="WEDDING_PLANNERS"><i class="bi bi-person-heart"></i><span>Wedding Planners</span></button>
          <button type="button" class="vendor-option" data-category="VENUES"><i class="bi bi-geo-alt"></i><span>Venues</span></button>
          <button type="button" class="vendor-option" data-category="MAKEUP_ARTISTS"><i class="bi bi-brush"></i><span>Makeup Artists</span></button>
          <button type="button" class="vendor-option" data-category="BRIDAL_SALONS"><i class="bi bi-gem"></i><span>Bridal Salons</span></button>
          <button type="button" class="vendor-option" data-category="HAIR_STYLISTS"><i class="bi bi-scissors"></i><span>Hair Stylists</span></button>
          <button type="button" class="vendor-option" data-category="PHOTO_BOOTH"><i class="bi bi-camera-reels"></i><span>Photo Booth</span></button>
          <button type="button" class="vendor-option" data-category="CONTENT_CREATOR"><i class="bi bi-camera-video"></i><span>Content Creator</span></button>
          <button type="button" class="vendor-option" data-category="WEDDING_OFFICIANTS"><i class="bi bi-person-badge"></i><span>Wedding Officiants</span></button>
          <button type="button" class="vendor-option" data-category="FLORISTS"><i class="bi bi-flower1"></i><span>Florists</span></button>
          <button type="button" class="vendor-option" data-category="OTHER"><i class="bi bi-three-dots"></i><span>Other Services</span></button>
        </div>
      </div>
      <button type="submit">Find Vendors <i class="bi bi-arrow-right"></i></button>
    </form>
    <p class="hero-note">Thoughtfully selected wedding services across Sri Lanka</p>
  </div>
  <div class="hero-caption">A DAY TO REMEMBER · ÉCLAT WEDDING DIRECTORY</div>
</section>

<section class="market-section category-section">
  <div class="section-heading"><p class="eyebrow">EXPLORE THE DIRECTORY</p><h2>Everything you need,<br><em>all in one place.</em></h2><p>Meet talented people who make every detail feel like you.</p></div>
  <div class="category-grid">
    <a class="category-tile" href="${pageContext.request.contextPath}/vendors?category=PHOTOGRAPHY"><img src="${pageContext.request.contextPath}/images/photography.jpg" alt="Wedding photography"><span>Photography</span></a>
    <a class="category-tile" href="${pageContext.request.contextPath}/vendors?category=WEDDING_PLANNERS"><img src="${pageContext.request.contextPath}/images/member02/wedding-arch.jpg" alt="Wedding planning"><span>Wedding Planners</span></a>
    <a class="category-tile" href="${pageContext.request.contextPath}/vendors?category=VENUES"><img src="${pageContext.request.contextPath}/images/venue.jpg" alt="Wedding venue"><span>Venues</span></a>
    <a class="category-tile" href="${pageContext.request.contextPath}/vendors?category=FLORISTS"><img src="${pageContext.request.contextPath}/images/florist.jpg" alt="Wedding flowers"><span>Florists & Decor</span></a>
    <a class="category-tile" href="${pageContext.request.contextPath}/vendors?category=BEAUTY_SALON"><img src="${pageContext.request.contextPath}/images/wedding-dress.jpg" alt="Bridal makeup"><span>Beauty</span></a>
    <a class="category-tile" href="${pageContext.request.contextPath}/vendors?category=CATERING"><img src="${pageContext.request.contextPath}/images/catering.jpg" alt="Wedding reception"><span>Catering</span></a>
  </div>
</section>

<section class="market-section featured-section">
  <div class="section-heading split-heading"><div><p class="eyebrow">THE ÉCLAT EDIT</p><h2>Meet our <em>featured vendors.</em></h2><p>Explore the professionals ready to make your day unforgettable.</p></div><a class="text-link" href="${pageContext.request.contextPath}/vendors">Explore all vendors <i class="bi bi-arrow-up-right"></i></a></div>
  <div class="vendor-feature-grid">
    <c:forEach var="v" items="${featuredVendors}">
      <article class="feature-vendor-card">
        <div class="feature-vendor-image">
          <c:choose>
            <c:when test="${v.category.name() == 'PHOTOGRAPHY' || v.category.name() == 'VIDEOGRAPHY' || v.category.name() == 'PHOTO_BOOTH' || v.category.name() == 'CONTENT_CREATOR'}"><img src="${pageContext.request.contextPath}/images/photography.jpg" alt="Photography portfolio" loading="lazy"></c:when>
            <c:when test="${v.category.name() == 'CATERING'}"><img src="${pageContext.request.contextPath}/images/catering.jpg" alt="Wedding catering portfolio" loading="lazy"></c:when>
            <c:when test="${v.category.name() == 'DECORATION' || v.category.name() == 'FLORISTS'}"><img src="${pageContext.request.contextPath}/images/decoration.jpg" alt="Wedding decor portfolio" loading="lazy"></c:when>
            <c:when test="${v.category.name() == 'MUSIC_DJ'}"><img src="${pageContext.request.contextPath}/images/music-dj.jpg" alt="Wedding DJ portfolio" loading="lazy"></c:when>
            <c:when test="${v.category.name() == 'WEDDING_DRESSES' || v.category.name() == 'BRIDAL_SALONS' || v.category.name() == 'MAKEUP_ARTISTS' || v.category.name() == 'HAIR_STYLISTS' || v.category.name() == 'BEAUTY_SALON'}"><img src="${pageContext.request.contextPath}/images/wedding-dress.jpg" alt="Bridal styling portfolio" loading="lazy"></c:when>
            <c:when test="${v.category.name() == 'VENUES'}"><img src="${pageContext.request.contextPath}/images/venue.jpg" alt="Wedding venue portfolio" loading="lazy"></c:when>
            <c:otherwise><img src="${pageContext.request.contextPath}/images/member02/wedding-arch.jpg" alt="Wedding service portfolio" loading="lazy"></c:otherwise>
          </c:choose>
          <span>${v.category.displayName}</span></div>
        <div class="feature-vendor-info"><div><h3>${v.vendorName}</h3><p><i class="bi bi-geo-alt"></i> ${v.address}</p></div><strong>LKR ${v.basePrice}</strong></div>
        <a class="vendor-view-link" href="${pageContext.request.contextPath}/vendors/view/${v.vendorId}">View profile <i class="bi bi-arrow-right"></i></a>
      </article>
    </c:forEach>
    <c:if test="${empty featuredVendors}">
      <div class="empty-feature"><p>Our curated vendor directory is growing.</p><a href="${pageContext.request.contextPath}/vendors">Browse the directory</a></div>
    </c:if>
  </div>
</section>

<section class="market-section destination-section" id="destinations">
  <div class="section-heading">
    <p class="eyebrow">SAY “I DO” IN SRI LANKA</p>
    <h2>Beautiful places for<br><em>your beginning.</em></h2>
    <p>From the southern coastline to the misty hill country, discover real Sri Lankan wedding destinations.</p>
  </div>
  <div class="destination-grid">
    <article class="destination-card">
      <a class="destination-image" href="https://www.jetwinghotels.com/jetwinglighthouse/weddings/" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/images/member02/wedding-beach.jpg" alt="Coastal wedding setting in Galle" loading="lazy"><span>Southern Coast</span></a>
      <div class="destination-copy"><p class="eyebrow">GALLE</p><h3>Jetwing Lighthouse</h3><p>Ocean views, coastal lawns and elegant halls beside the historic city of Galle.</p><a href="https://www.jetwinghotels.com/jetwinglighthouse/weddings/" target="_blank" rel="noopener">Explore wedding venue <i class="bi bi-arrow-up-right"></i></a></div>
    </article>
    <article class="destination-card">
      <a class="destination-image" href="https://www.heritancehotels.com/kandalama/weddings/" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/images/member02/wedding-garden.jpg" alt="Garden wedding setting surrounded by nature" loading="lazy"><span>Cultural Triangle</span></a>
      <div class="destination-copy"><p class="eyebrow">DAMBULLA · SIGIRIYA</p><h3>Heritance Kandalama</h3><p>A nature-rich celebration framed by forest, water and the Sigiriya landscape.</p><a href="https://www.heritancehotels.com/kandalama/weddings/" target="_blank" rel="noopener">Explore wedding venue <i class="bi bi-arrow-up-right"></i></a></div>
    </article>
    <article class="destination-card">
      <a class="destination-image" href="https://www.anantara.com/en/peace-haven-tangalle/weddings" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/images/member02/wedding-beach.jpg" alt="Beach wedding inspiration on Sri Lanka's southern coast" loading="lazy"><span>Oceanfront Escape</span></a>
      <div class="destination-copy"><p class="eyebrow">TANGALLE</p><h3>Anantara Peace Haven</h3><p>Clifftop ocean views and tropical gardens for a memorable destination celebration.</p><a href="https://www.anantara.com/en/peace-haven-tangalle/weddings" target="_blank" rel="noopener">Explore wedding venue <i class="bi bi-arrow-up-right"></i></a></div>
    </article>
    <article class="destination-card">
      <a class="destination-image" href="https://www.jetwinghotels.com/jetwingblue/" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/images/member02/wedding-ballroom.jpg" alt="Elegant wedding reception venue" loading="lazy"><span>West Coast</span></a>
      <div class="destination-copy"><p class="eyebrow">NEGOMBO</p><h3>Jetwing Blue</h3><p>A relaxed coastal setting with spaces for celebrations by the sea.</p><a href="https://www.jetwinghotels.com/jetwingblue/" target="_blank" rel="noopener">Explore hotel <i class="bi bi-arrow-up-right"></i></a></div>
    </article>
    <article class="destination-card">
      <a class="destination-image" href="https://www.heritancehotels.com/teafactory/" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/images/member02/wedding-classic.jpg" alt="Romantic highland wedding inspiration" loading="lazy"><span>Hill Country</span></a>
      <div class="destination-copy"><p class="eyebrow">NUWARA ELIYA</p><h3>Heritance Tea Factory</h3><p>A distinctive highland atmosphere surrounded by tea country and misty hills.</p><a href="https://www.heritancehotels.com/teafactory/" target="_blank" rel="noopener">Explore hotel <i class="bi bi-arrow-up-right"></i></a></div>
    </article>
    <article class="destination-card">
      <a class="destination-image" href="https://www.gallefacehotel.com/" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/images/member02/wedding-ballroom.jpg" alt="Classic hotel wedding reception inspiration in Colombo" loading="lazy"><span>City Heritage</span></a>
      <div class="destination-copy"><p class="eyebrow">COLOMBO</p><h3>The Galle Face Hotel</h3><p>Colonial character and a timeless seaside city setting for a classic celebration.</p><a href="https://www.gallefacehotel.com/" target="_blank" rel="noopener">Explore hotel <i class="bi bi-arrow-up-right"></i></a></div>
    </article>
  </div>
  <p class="destination-disclaimer">Venue details and wedding arrangements are subject to confirmation directly with each hotel.</p>
</section>

<section class="market-section inspiration-section" id="inspiration">
  <div class="section-heading"><p class="eyebrow">IDEAS TO FALL IN LOVE WITH</p><h2>Make it <em>meaningful.</em></h2><p>Wedding themes and little details to inspire a celebration that feels like you.</p></div>
  <div class="inspiration-grid">
    <article><img src="${pageContext.request.contextPath}/images/member02/wedding-traditional.jpg" alt="Traditional Sri Lankan wedding inspiration" loading="lazy"><div><small>STYLE GUIDE · SRI LANKAN TRADITION</small><h3>A celebration rooted in tradition</h3><p>Poruwa details, warm gold tones and meaningful family moments create a day filled with heritage.</p></div></article>
    <article><img src="${pageContext.request.contextPath}/images/member02/wedding-flowers.jpg" alt="Garden wedding floral styling inspiration" loading="lazy"><div><small>STYLE GUIDE · GARDEN ROMANCE</small><h3>Flowers, soft light and garden vows</h3><p>Natural textures, seasonal blooms and gentle colours bring an outdoor celebration to life.</p></div></article>
    <article><img src="${pageContext.request.contextPath}/images/member02/wedding-beach.jpg" alt="Beach wedding styling inspiration" loading="lazy"><div><small>STYLE GUIDE · COASTAL ELEGANCE</small><h3>Say hello to a seaside celebration</h3><p>Ocean views, airy fabrics and sunset-inspired details set the mood for a relaxed coastal day.</p></div></article>
  </div>
</section>
<section class="market-section sample-stories-section" id="stories">
  <div class="section-heading"><p class="eyebrow">THE ÉCLAT STORYBOOK</p><h2>Little stories, <em>big feelings.</em></h2><p>Illustrative sample stories created to show the kinds of celebrations couples can plan with Éclat.</p></div>
  <div class="sample-story-grid" id="real-weddings">
    <article><img src="${pageContext.request.contextPath}/images/member02/wedding-classic.jpg" alt="Classic wedding inspiration" loading="lazy"><div><span>DEMO STORY · COLOMBO</span><h3>An intimate city celebration</h3><p>A small guest list, a timeless palette and thoughtful details made this sample celebration feel personal and warm.</p></div></article>
    <article><img src="${pageContext.request.contextPath}/images/member02/wedding-garden.jpg" alt="Garden wedding inspiration" loading="lazy"><div><span>DEMO STORY · KANDY</span><h3>A garden filled with colour</h3><p>This fictional inspiration story imagines a garden ceremony with fresh florals, soft music and a relaxed reception.</p></div></article>
    <article><img src="${pageContext.request.contextPath}/images/member02/wedding-beach.jpg" alt="Coastal wedding inspiration" loading="lazy"><div><span>DEMO STORY · SOUTH COAST</span><h3>Golden hour by the ocean</h3><p>A sample destination-wedding idea with coastal views, candlelight and an evening celebration under the stars.</p></div></article>
  </div>
  <p class="destination-disclaimer">These are fictional demonstration stories, not real customer reviews or verified weddings.</p>
</section>

<section class="market-cta"><p class="eyebrow light">YOUR NEXT CHAPTER STARTS HERE</p><h2>Let the planning<br><em>feel as special as the day.</em></h2><a href="${pageContext.request.contextPath}/register">Create your free account <i class="bi bi-arrow-right"></i></a></section>
</main>
<footer class="market-footer"><a class="market-brand" href="${pageContext.request.contextPath}/">Éclat<span>.</span></a><p>Wedding Planning & Vendor Booking · SE1020 OOP Project</p><div><a href="${pageContext.request.contextPath}/vendors">Vendors</a><a href="${pageContext.request.contextPath}/login">Log In</a><a href="${pageContext.request.contextPath}/register">Sign Up</a></div></footer>

<script>
(() => {
  const mobileToggle = document.querySelector('.market-menu-toggle');
  const mobileLinks = document.getElementById('marketLinks');
  if (mobileToggle && mobileLinks) {
    mobileToggle.addEventListener('click', () => {
      const expanded = mobileToggle.getAttribute('aria-expanded') === 'true';
      mobileToggle.setAttribute('aria-expanded', String(!expanded));
      mobileLinks.classList.toggle('show', !expanded);
    });
    mobileLinks.querySelectorAll('a').forEach(link => link.addEventListener('click', () => {
      mobileLinks.classList.remove('show'); mobileToggle.setAttribute('aria-expanded', 'false');
    }));
  }
  const trigger = document.getElementById('vendorPickerTrigger');
  const menu = document.getElementById('vendorPickerMenu');
  const label = document.getElementById('vendorPickerLabel');
  const hidden = document.getElementById('selectedVendorCategory');
  if (!trigger || !menu || !label || !hidden) return;

  const closeMenu = () => {
    menu.hidden = true;
    trigger.setAttribute('aria-expanded', 'false');
  };
  trigger.addEventListener('click', (event) => {
    event.stopPropagation();
    const willOpen = menu.hidden;
    menu.hidden = !willOpen;
    trigger.setAttribute('aria-expanded', String(willOpen));
  });
  menu.querySelectorAll('.vendor-option').forEach((option) => {
    option.addEventListener('click', () => {
      hidden.value = option.dataset.category || '';
      label.textContent = option.querySelector('span').textContent;
      menu.querySelectorAll('.vendor-option').forEach(item => item.classList.remove('selected'));
      option.classList.add('selected');
      closeMenu();
    });
  });
  document.addEventListener('click', (event) => {
    if (!event.target.closest('.vendor-picker')) closeMenu();
  });
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') closeMenu();
  });
})();
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body></html>