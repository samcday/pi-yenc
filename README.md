# Introduction

**Pi yEnc** is a small Java library to work with the [yEnc encoding](http://www.yenc.org/yenc-draft.1.3.txt).

# Quick start

## Reading yEnc-encoded streams

The yEnc encoding specifies how multiple entries can be encoded in a stream.

`YEncInputStream` is a specialized `InputStream` that reads entries and their contents from an `InputStream`.

Entries are represented as instances of `YEncEntry`. They are read with the `getNextEntry` method (that returns `null` if there are no more entries in the stream). Once an entry has been read, the `YEncInputStream` behaves as an `InputStream` on the contents of that entry.

    YEncInputStream yEncIn = new YEncInputStream(new FileInputStream("example.yenc"));

    // Read first entry
    YEncEntry entry = yEncIn.getNextEntry();
    // ...

    // Read contents of first entry
    while (true) {
        int b = yEncIn.read();
        if (b == -1) {
            // End of contents of first entry
            break;
        }
        // ...
    }

    // Read next entry
    entry = yEncIn.getNextEntry();
    if (entry == null) {
        // No more entries in yEncIn
        // ...
    }

    // ...

## Reading yEnc subject lines

The yEnc encoding also specifies the format of *subject lines*.

The `YEncSubject` class encapsulates the informations available in subject lines, and provides methods to parse them.

    YEncSubject subject = YEncSubject.parseSubject("Provided by Pi Solutions \"Document.pdf\" yEnc Public Domain");
    subject.getFileName(); // "Document.pdf"
    subject.getComment1(); // "Provided by Pi Solutions"
    subject.getComment2(); // "Public Domain"
    // ...
