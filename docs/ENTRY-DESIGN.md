
  [ number of units in order]            [ Pack = the number of items in each unit ]
      \ /                                             \ /
Count (?)    | Item                      | Unit (Pack (?))        | Unit Weight        | Unit Cost      | Total Weight | Total Cost
=========================================================================================================================================
<!-- Examples from invoice #1 -->
26           | Peaches Canned            | Case (24 count)        | 24lb               | $46            | ...          | ...
38           | Fresh Grapes Variety      | Case                   | 19lb               | $16            | ...          | ...

<!-- Examples from invoice #3 -->
28           | Green Beans Can (15 oz)   | Case (24 count)        | (8oz * 12) = 12lb  | $15.60         | ...          | ...
28           | Chunk Tuna Can (4 oz)     | Case (48 count)        | (4oz * 48) = 24lb  | $31.39         | ...          | ...

<!-- Example from invoice #2 -->
2            | Green Cabbage, Whole Head | Crate                  | 45lb               | $33.25        | ...          | ...
2            | Romaine Lettuce, Head     | Case (24 count)        | 40lb               | $24.75         | ...          | ...
<!-- 3 different ways of expressing the same quantity of food -->
3            | Russet Potato Bag (50 lb) | Item (1 count)         | (50lb * 1) = 50lb  | $15            | 150 lb       | $45
3            | Russet Potato             | Sack                   | 50lb               | $10            | 150 lb       | $30
150          | Russet Potato             | Bulk                   | 1lb                | $0.15          | 150 lb       | $22.50


- `Item`s can optionally have a (net) `weight` associated with them
  - This is used to calculate the total weight for an entry
  - Some items may not have a weight (e.g., head of lettuce, loose grapes)
- For each `Entry` the user must supply:
  - An `Item` with a weight, AND A 'pack' value (no unit weight),
  OR
  - An `Item` without a weight, (optional 'pack' value), AND a (net) 'unit weight'

- Add some `(?)` tooltips to the fields to keep things clear
