// -> src/main/java/com/jbase/core/index/NodeSerializer.java

## Serialization

* Converts structured node fields into a flat byte layout

* Uses lengths to support variable-size keys/values

* Everything fits into one page

## Deserialization

* Reads bytes back in the same order

* Reconstructs a BTreeNode

* Node becomes a temporary in-memory view