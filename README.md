# A Digital Norwegian/Bokmål→German Dictionary

A term project for the course "Computational Lexicography on the Web" at the University of Tübingen (WiSe 2018/19).

## Introduction

There are few existing digital tools for Norwegian, including dictionaries from Norwegian to German. Those that do exist are designed for a very straightforward look-up of lemmas. The dictionary built in the context of this course project however provides additional functionalities:  Looking up words in a text without breaking the reading flow of the user,
- Lemmatization, also of irregularly inflected entries (which, due to umlaut mutations, is not necessarily trivial for a learner of the language),
- Compound splitting,
- Providing sample sentences to present entries in additional context,
- Pronunciation information.
This online dictionary is intended as a 'passive' dictionary that helps German speakers understand text written in Norwegian (Bokmål). It is built
using the Google Web Toolkit (GWT) framework.

The [report](/doc/Report.pdf) contains more information on lexicographical considerations, the input data (incl. preprocessing and unification details), the GUI and how user queries are processed.

## Screenshots

To get an impression of the UI, here are the input widget and a sample output (for part of the Norwegian Wikipedia entry on dictionaries: https://no.wikipedia.org/wiki/Ordbok, CC BY-SA 3.0, last accessed Aug 25th, 2019). More examples can be found in the report.

! [Input](/doc/input-2.PNG)

! [Output](/doc/output.PNG)