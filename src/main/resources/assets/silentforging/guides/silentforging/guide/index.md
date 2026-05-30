---
navigation:
  title: Forging Tools
  icon: silentforging:tool_forge
item_ids:
  - silentforging:tool_forge
---

# Forging With the Tool Forge

<ItemImage id="silentforging:tool_forge" scale="2" />

The **Tool Forge** turns raw materials into Silent Gear parts through a
short forging minigame. Instead of crafting a part instantly, you shape
a heat bar with hammer strokes and aim for the recipe's target.

## Setup

1. Place the **Tool Forge** and right-click it to open the screen.
2. Put a Silent Gear **blueprint** in the blueprint slot.
3. Put a matching **ingot or gem** in the material slot.

The screen then loads that part's **target** and shows the forge bar.

## The Six Strokes

Press keys **1-6** to apply a stroke. Each one shifts the bar by a fixed
amount — three raise it, three lower it:

- **Hit** (+18) · **Bend** (+9) · **Draw** (+7)
- **Extrude** (-14) · **Fold** (-11) · **Temper** (-4)

The bar is clamped between 0 and 100. A part is finished once the bar
lands within the recipe's allowed **range** of its target.

## Quality

Quality is about **efficiency**, not just hitting the target. The fewer
extra strokes you use to land in range, the better the part:

- **Pristine** — reached in the minimum strokes (**+10%** stats)
- **Normal** — a few strokes over (no change)
- **Crude** — well over the minimum (**-10%** stats)

Plan a route to the target with big strokes, then nudge with small ones.
The finished part drops into the output slot, ready for normal Silent
Gear assembly.
