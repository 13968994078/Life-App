# Food Wheel Dynamic Geometry Design

## Goal

Fix the "今日吃什么" wheel so any dynamic item count, including 7 items, produces consistent color sectors, divider lines, labels, and pointer landing behavior.

## Scope

- Unify wheel angle calculations behind one shared geometry helper.
- Keep wheel sectors dynamic based on the current item count.
- Freeze the displayed item set for the duration of a spin.
- Disable category switching and food mutations while the wheel is spinning.
- Add a lightweight local verification script for geometry math.

## Design

### Shared geometry

Create a small helper module under `frontend/utils/` that owns:

- item count normalization
- sector angle calculation
- sector start/end/center angles
- divider angle calculation
- label anchor angle calculation
- wheel rotation needed to place a given index under the top pointer

All wheel visuals and spin math must read from this helper instead of recomputing angles inline.

### Rendering behavior

`frontend/components/food-wheel.vue` will render sectors from a conic gradient whose ranges come from the shared helper. Divider lines and labels will use the same center/start angles so odd counts and dynamic counts stay aligned.

### Spin-time locking

`frontend/pages/food/index.vue` will treat `spinning` as a UI lock:

- ignore category picker changes
- ignore submit requests
- ignore delete requests
- keep the wheel item list fixed until the spin finishes

This prevents mid-animation item-count changes from invalidating the current geometry.

## Verification

Add a Node script that asserts:

- 7 items produce equal sectors that fully cover 360 degrees
- each sector center is the midpoint of its start/end angles
- divider angles line up with sector boundaries
- pointer landing math resolves a target index to the top pointer consistently

## Notes

This workspace currently does not expose a verified frontend test runner, so verification will use a standalone Node script instead of a framework test.
