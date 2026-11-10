# Éclat UI refresh — 10 November 2026

## Design direction
A refined editorial wedding-planning interface: warm ivory canvas, calm charcoal typography, restrained champagne-gold accents, generous whitespace, and subtle surfaces. The existing Éclat brand and Playfair Display / DM Sans pairing are retained.

## What changed
- Added `eclat-ui-2026.css`, a shared design layer loaded after existing styles.
- Standardised core application navigation, cards, buttons, forms, tables, status badges, alerts, pagination and focus states.
- Improved responsive spacing for dashboard and module pages, including narrower screens.
- Refined the public landing hero, search surface, vendor cards and imagery interactions.
- Replaced the landing page's inline-only mobile menu toggle with an accessible button that updates `aria-expanded`, closes after navigation and supports keyboard interaction.
- Added reduced-motion and print-friendly rules.
- Added clearer dashboard metric labels without changing controller model attributes or server-side workflows.

## Compatibility
This is intentionally a progressive enhancement of the existing Spring Boot + JSP + Bootstrap application. It does not replace the rendering stack, change routes, alter persistence, or introduce a new JavaScript framework. Existing module-specific styles still load before the shared final layer.

## Validation
The GitHub Actions Maven workflow should be treated as the merge gate. A browser smoke test is still required for public home, login/register/OTP, dashboard, each member module, mobile navigation, vendor picker, and CSRF-protected POST forms. Visual accessibility should be checked with keyboard-only navigation and reduced-motion enabled.

## Next-stage recommendations
- Introduce a small icon/empty-state component library once module pages have been visually smoke-tested.
- Remove obsolete duplicate CSS overrides gradually rather than deleting legacy selectors in one pass.
- Consider self-hosting and subsetting the web fonts, and converting large raster images to WebP/AVIF after checking actual deployment CSP and caching.
- Add automated visual regression screenshots at 360px, 768px, 1024px and 1440px.
