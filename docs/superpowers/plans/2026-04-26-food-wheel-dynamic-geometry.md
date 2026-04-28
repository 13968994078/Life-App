# Food Wheel Dynamic Geometry Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix the food wheel so dynamic item counts always render aligned sectors and the wheel stays stable while spinning.

**Architecture:** Extract wheel-angle math into a shared helper, then make both the wheel component and page spin logic consume that helper. Treat `spinning` as a lock that freezes item mutations during animation.

**Tech Stack:** uni-app Vue SFCs, plain JavaScript helpers, Node for local verification

---

### Task 1: Add failing geometry verification

**Files:**
- Create: `frontend/scripts/verify-food-wheel-geometry.js`
- Test: `frontend/scripts/verify-food-wheel-geometry.js`

- [ ] **Step 1: Write the failing test**
- [ ] **Step 2: Run `node frontend/scripts/verify-food-wheel-geometry.js` and confirm it fails because the helper does not exist yet**

### Task 2: Implement shared wheel geometry

**Files:**
- Create: `frontend/utils/wheel-geometry.js`
- Modify: `frontend/components/food-wheel.vue`
- Modify: `frontend/pages/food/index.vue`

- [ ] **Step 1: Add helper functions for sector ranges, divider angles, label angles, and target rotation**
- [ ] **Step 2: Update `food-wheel.vue` to render sectors and labels from the helper**
- [ ] **Step 3: Update `pages/food/index.vue` to compute pointer landing from the helper**

### Task 3: Prevent spin-time item mutations

**Files:**
- Modify: `frontend/pages/food/index.vue`

- [ ] **Step 1: Ignore category changes while spinning**
- [ ] **Step 2: Ignore submit and delete actions while spinning**
- [ ] **Step 3: Leave the current wheel item list untouched until the spin ends**

### Task 4: Verify and document

**Files:**
- Test: `frontend/scripts/verify-food-wheel-geometry.js`

- [ ] **Step 1: Run `node frontend/scripts/verify-food-wheel-geometry.js` and confirm it passes**
- [ ] **Step 2: Summarize the verification result and any remaining manual UI checks**
