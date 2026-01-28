## Logic (what’s happening)

### Root setup

* Tree starts with **one empty leaf**
* Root is identified by a **page ID**
* Node lives in memory via `NodeCache` (temporary stand-in for page bytes)

---

### Search flow

1. Start at `rootPageId`
2. Binary-search keys in the node
3. If leaf:

   * Key match → return value
   * No match → `null`
4. If internal:

   * Follow child pointer (page ID)
   * Recurse

This is **exact B+ tree search logic**, just without persistence.


