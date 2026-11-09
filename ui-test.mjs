import { chromium } from 'playwright';

const BASE_URL = process.env.BASE_URL || 'http://127.0.0.1:8080';
const ADMIN_EMAIL = process.env.ADMIN_EMAIL || '';
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || '';

const results = [];
const testedUrls = new Set();

function record(name, status, detail = '') {
    results.push({ name, status, detail });
    console.log(`${status === 'PASS' ? '✅' : status === 'SKIP' ? '⚠️' : '❌'} ${name} ${detail}`);
}

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage();
page.setDefaultTimeout(10000);

page.on('pageerror', error => {
    record('Browser JavaScript error', 'FAIL', error.message);
});

async function visit(url, label) {
    if (testedUrls.has(url)) return;
    testedUrls.add(url);

    try {
        const response = await page.goto(url, { waitUntil: 'domcontentloaded' });
        const status = response?.status() ?? 0;

        if (status >= 500 || status === 0) {
            record(label, 'FAIL', `HTTP ${status}`);
        } else {
            record(label, 'PASS', `HTTP ${status} | ${page.url()}`);
        }
    } catch (error) {
        record(label, 'FAIL', error.message);
    }
}

try {
    console.log('\nÉCLAT UI AUTOMATED TEST');
    console.log('Base URL:', BASE_URL, '\n');

    // 1. Home page
    await visit(BASE_URL, 'Home page');

    // 2. Login page
    await visit(`${BASE_URL}/login`, 'Login page');

    // 3. Test login role tabs
    for (const selector of [
        '#admin-tab',
        '#customer-tab'
    ]) {
        try {
            const tab = page.locator(selector);
            if (await tab.count()) {
                await tab.click();
                record(`Login tab ${selector}`, 'PASS');
            } else {
                record(`Login tab ${selector}`, 'SKIP', 'Not found');
            }
        } catch (error) {
            record(`Login tab ${selector}`, 'FAIL', error.message);
        }
    }

    // 4. Check login forms and inputs
    for (const selector of [
        'form[action*="/login"]',
        '#customerEmail',
        '#customerPassword',
        '#adminEmail',
        '#adminPassword'
    ]) {
        const count = await page.locator(selector).count();
        record(`Login element ${selector}`, count ? 'PASS' : 'FAIL',
            `${count} found`);
    }

    // 5. Optional admin login
    if (ADMIN_EMAIL && ADMIN_PASSWORD) {
        try {
            await page.locator('#admin-tab').click();
            await page.locator('#adminEmail').fill(ADMIN_EMAIL);
            await page.locator('#adminPassword').fill(ADMIN_PASSWORD);
            await page.locator('#adminPane button[type="submit"]').click();
            await page.waitForLoadState('domcontentloaded');

            if (page.url().includes('/admin/dashboard')) {
                record('Admin login', 'PASS', page.url());
            } else {
                record('Admin login', 'FAIL', `Redirected to ${page.url()}`);
            }
        } catch (error) {
            record('Admin login', 'FAIL', error.message);
        }
    } else {
        record('Admin login', 'SKIP', 'Set ADMIN_EMAIL and ADMIN_PASSWORD to test');
    }

    // 6. Test internal navigation links from current page
    const links = await page.locator('a[href]').evaluateAll(anchors =>
        anchors.map(a => ({
            text: (a.innerText || a.textContent || '').trim().replace(/\s+/g, ' '),
            href: a.href
        }))
    );

    const origin = new URL(BASE_URL).origin;
    const safeLinks = [...new Map(
        links.filter(link => {
            try {
                const u = new URL(link.href);
                return u.origin === origin &&
                    !/logout|delete|remove|signout/i.test(link.href);
            } catch {
                return false;
            }
        }).map(link => [link.href, link])
    ).values()];

    for (const link of safeLinks) {
        await visit(link.href, `Navigation: ${link.text || link.href}`);
    }

    // 7. Check buttons and forms on the current page
    await page.goto(BASE_URL, { waitUntil: 'domcontentloaded' });

    const buttonCount = await page.locator('button').count();
    const formCount = await page.locator('form').count();
    const inputCount = await page.locator('input').count();

    record('Button elements detected', 'PASS', `${buttonCount} buttons`);
    record('Form elements detected', 'PASS', `${formCount} forms`);
    record('Input elements detected', 'PASS', `${inputCount} inputs`);

} catch (error) {
    record('Test runner', 'FAIL', error.message);
} finally {
    await browser.close();

    const passed = results.filter(r => r.status === 'PASS').length;
    const failed = results.filter(r => r.status === 'FAIL').length;
    const skipped = results.filter(r => r.status === 'SKIP').length;

    console.log('\n========== FINAL TEST REPORT ==========');
    console.log(`PASSED:  ${passed}`);
    console.log(`FAILED:  ${failed}`);
    console.log(`SKIPPED: ${skipped}`);
    console.log(`TOTAL:   ${results.length}`);
    console.log('=======================================\n');

    process.exitCode = failed > 0 ? 1 : 0;
}
