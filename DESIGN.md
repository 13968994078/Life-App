# DESIGN.md

## 1. Visual Theme & Atmosphere

- Overall mood: calm, tidy, warm, quietly competent.
- Product feeling: a daily life tool with document-product discipline, not a marketing page.
- Core impression: paper-like surfaces, soft edges, restrained contrast, and deliberate spacing.
- Visual density: low to medium. Screens should feel organized before they feel decorative.
- Interaction tone: quiet and reassuring, never loud, playful, or sales-driven.
- Design inspiration: mostly Notion-style warm minimalism, adapted for a lifestyle utility app.
- First impression priority: readable hierarchy, steady rhythm, and trustworthy everyday usefulness.

## 2. Color Palette & Roles

### Core neutrals

- `Background / Page`: `#F8F5F1`
- `Surface / Card`: `#FFFFFF`
- `Surface / Soft`: `#F3EEE8`
- `Surface / Pressed`: `#ECE5DD`
- `Border / Light`: `#E3DBD2`
- `Border / Strong`: `#D2C7BB`
- `Text / Primary`: `#2B2622`
- `Text / Secondary`: `#6F665E`
- `Text / Muted`: `#9A9087`

### Feature accents

- `Primary Accent`: `#D97757`
- `Primary Accent Deep`: `#C9623F`
- `Warm Highlight`: `#F6D9B8`
- `Check-in Accent`: `#7D93D6`
- `Check-in Accent Deep`: `#6179C5`

### Status colors

- `Success`: `#2E8B57`
- `Success Surface`: `#EAF7EF`
- `Warning`: `#C57A1C`
- `Warning Surface`: `#FFF4E5`
- `Danger`: `#D2553F`
- `Danger Surface`: `#FDEDEA`

## 3. Typography Rules

- Preferred font stack: clean system UI fonts for most text.
- Chinese text should feel natural and readable, not overly condensed.
- Headings may use a softer editorial feel, but readability always wins over personality.
- Title style: firm hierarchy, moderate weight, never oversized for the sake of drama.
- Body text: soft contrast, comfortable line height, and stable rhythm across cards and lists.
- Avoid ultra-thin weights, overly geometric display type, and compressed text.

### Type scale

- `Hero Title`: `48rpx`, weight `700`, line-height `1.3`
- `Page Title`: `40-42rpx`, weight `700`
- `Section Title`: `34-36rpx`, weight `700`
- `Card Title`: `30-32rpx`, weight `700`
- `Body`: `26-28rpx`, weight `400`
- `Meta / Hint`: `22-24rpx`, weight `400-500`
- `Chip / Status`: `22-24rpx`, weight `600`

### Heading behavior

- Use tighter hierarchy rather than bigger sizes to create emphasis.
- Limit each screen to one true title and one secondary section emphasis style.
- Metadata should stay visibly quieter than primary content at all times.

## 4. Component Stylings

### Cards

- Use rounded white cards as the primary content container.
- Radius: `24-28rpx`.
- Shadow: soft, diffused, low contrast.
- Cards should feel like floating paper rather than glossy panels.
- Prefer thin borders plus very soft shadow, not shadow-only separation.
- Inside cards, use strong vertical rhythm and avoid overly segmented mini-panels.

### Buttons

- Primary buttons: solid warm accent preferred over obvious gradients.
- Text should always be high contrast white.
- Radius: pill shape preferred for primary actions.
- Secondary buttons: tinted neutral background, low emphasis.
- Danger buttons: text-first or soft-tinted, never harsh solid red unless destructive confirmation is final.
- Pressed state should darken slightly rather than animate dramatically.
- Keep one dominant primary action per viewport section.

### Inputs

- Soft filled background, not pure white outline-only fields.
- Use subtle warm-neutral field fill.
- Border should be faint and understated.
- Touch area must feel mobile-friendly and generous.
- Focus state should rely on clearer border contrast, not glow-heavy effects.

### Tags and Chips

- Rounded pill chips with soft tint background.
- Category chips can use warm accent tint.
- Status chips should use semantic surface colors.

### Lists

- Rows should feel roomy and mobile-first.
- Keep strong visual separation between primary content and metadata.
- Use subtle dividers only when needed.
- Treat list items like quiet structured documents, not dashboard rows.

### Banners / Hero Blocks

- One strong banner per page is acceptable.
- Banner should set page mood, not compete with content.
- Use gentle gradients with clear text hierarchy.

### Navigation and Tabs

- Navigation should feel plain, stable, and obvious.
- Active states should use background tint or weight shift, not loud color flooding.
- Tab bars and section tabs should preserve generous spacing and low visual noise.

### Empty, Loading, and Feedback States

- Empty states should feel helpful and quiet, with one clear next action.
- Loading states should prefer skeleton blocks or subtle placeholders over spinners when possible.
- Success feedback should feel understated; confirmation should not interrupt flow unless important.
- Error feedback should be clear, compact, and actionable without creating panic.

## 5. Layout Principles

- Prioritize vertical flow and single-column mobile layout.
- Default page padding: around `28-32rpx`.
- Prefer consistent card stacking with `20-24rpx` gaps.
- Avoid dense grids unless content truly benefits from them.
- Most pages should read top-to-bottom in one pass.
- Use alignment discipline: left edges should line up cleanly across headers, cards, and list content.

### Spacing rhythm

- `XS`: `8rpx`
- `S`: `12rpx`
- `M`: `16rpx`
- `L`: `20rpx`
- `XL`: `24rpx`
- `2XL`: `32rpx`

## 6. Depth & Elevation

- Depth is soft and minimal.
- Prefer one consistent card shadow system across the app.
- Avoid sharp dark shadows.
- Do not stack too many elevated layers inside one screen.
- Favor contrast through spacing, border, and surface tone before adding more elevation.

## 7. Do's and Don'ts

### Do

- Keep screens calm and readable.
- Use warmth for food-related actions and cool tones for check-in and habit tracking.
- Let whitespace create quality.
- Keep important actions obvious.
- Make the interface feel trustworthy and daily-use friendly.
- Let typography and spacing do more work than color.

### Don't

- Don’t use neon colors, extreme contrast, or cyberpunk styling.
- Don’t overcrowd cards with too many badges or icons.
- Don’t use excessive gradients on every element.
- Don’t make the app feel like a finance dashboard.
- Don’t mix too many visual metaphors across pages.
- Don’t turn warm minimalism into beige blur; hierarchy must remain crisp.

## 8. Responsive Behavior

- Primary target is mobile app layout.
- All screens should work cleanly on narrow phone widths first.
- Content should avoid horizontal scrolling.
- Tap targets should be at least comfortably thumb-sized.
- Long lists should remain visually light and easy to scan.

## 9. Page-Specific Guidance

### Home

- Feel welcoming, simple, and immediately legible.
- One hero area plus clear entry cards is enough.
- Avoid cluttering the first screen.
- Prioritize quick orientation: what this app helps with and where to go next.

### Random Food

- Warmest page in the app.
- The wheel or selection area should feel playful but still clean.
- Food list and history should remain structured and easy to scan.
- Playfulness should come from composition and accent placement, not novelty UI effects.

### Check-in

- Cooler, more morning-oriented tone.
- Emphasize progress, consistency, and calm motivation.
- Status and streak information should be immediately legible.
- Progress modules should feel methodical and reassuring, not competitive.

### Mine / Settings

- Quiet and utility-first.
- Use restrained visuals and clear form controls.
- Settings should feel stable and trustworthy.
- Account, token, and API configuration areas should read like structured forms, not promotional cards.

## 10. Agent Prompt Guide

- If generating new UI, prefer: “Notion-inspired warm minimal layout, mobile-first lifestyle utility app, paper-like cards, restrained contrast, clean spacing, warm food accent, cool habit-tracking accent.”
- If designing food features, bias toward warm orange, cream, and soft beige tones.
- If designing check-in features, bias toward light blue accents with calm neutral surfaces.
- When in doubt, simplify rather than decorate.
- Prefer disciplined alignment, quiet hierarchy, and soft surfaces over decorative novelty.

### Prompt examples

- Home page: “Design a mobile-first home screen for a daily life utility app. Use a Notion-inspired warm minimal style with paper-like cards, calm cream background, clear vertical rhythm, one welcoming hero block, and tidy entry cards for food picker, check-in, and settings.”
- Random food page: “Design a random food selection page in a warm, restrained lifestyle-tool style. Keep the selection area playful but clean, use soft orange accents, rounded white cards, structured list sections, and avoid game-like or flashy visuals.”
- Check-in page: “Design a mobile check-in page with calm morning energy. Use soft neutral surfaces with muted blue accents, make streak and completion status immediately legible, and present progress in a methodical, reassuring way rather than a competitive dashboard style.”
- Settings page: “Design a quiet, trustworthy settings page for a mobile utility app. Use low-noise form rows, soft filled inputs, subtle section grouping, and clear hierarchy similar to a warm document product rather than a consumer marketing page.”
- Empty state: “Create an empty state that feels helpful and calm. Use one concise explanation, one clear next action, soft neutral surfaces, and no mascot, joke, or attention-grabbing illustration unless explicitly requested.”
- List page: “Design a mobile list page with roomy rows, strong separation between primary content and metadata, subtle dividers, aligned left edges, and a soft paper-card presentation inspired by Notion.”
