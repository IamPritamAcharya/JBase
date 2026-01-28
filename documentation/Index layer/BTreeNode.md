// ref -> src/main/java/com/jbase/core/index/BTreeNode.java

**Code** | **Logic**
---------|----------
`isLeaf` | B+ trees treat leaf and internal nodes differently
`keys` | Keys are always sorted, used for binary search
`values` | Only leaf nodes store values (classic B+ tree)
`children` | Internal nodes store page IDs, not object refs.
`nextLeaf` | Enables fast range scans ( leaf chaining )

**Internal Node**
```
keys:     [10 | 20 | 30]
children: [ 3 | 7 | 9 | 12 ]  ← page IDs
```
**Leaf node**
```
keys:   [10 | 12 | 15]
values: [ A |  B |  C ]
nextLeaf → page 14
```