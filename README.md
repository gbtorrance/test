# TestDataCreator
TestDataCreator (TDC) is a tool designed to help non-technical users easily create complex XML files with nothing more than a basic understanding of Microsoft Excel.

![screenshot1](https://cloud.githubusercontent.com/assets/16735709/13554422/742b89f0-e375-11e5-9f1a-1164e6ebb730.JPG)

## An origin story

TestDataCreator was originally created to support the testing of a State Revenue agency's 'Modernized eFile' ('MeF') implementation. At the most basic level, MeF is a standard defining how electronically-filed tax returns should be submitted to the IRS or State Revenue agencies. It was created by the IRS to support the filing of Federal returns, and was later adopted by State Revenue agencies for the filing of State returns (http://www.statemef.com/). There are currently a number of implementations of the MeF standard in use at various State Revenue agencies.

If you have ever filed a tax return using TurboTax, TaxAct, or any of the thousands of tax-preparer services that spring up like weeds during the first few months of every tax year, then your return was likely processed by an MeF system.

MeF tax returns are made up of one or more -- often extremely complex -- XML tax-return files. To properly test an MeF system, hundreds of these files may be needed. Someone with a technical background, and knowledge of XML, could certainly create the files (given enough time). But technical resources are often limited, and often don't have an in-depth understanding of the business domain (in this case, taxation). It is no easy task to find someone able to manually create complex XML files *and* understand the deep magic of tax law. Do such creatures even exist?

TestDataCreator was created to address this need. Most people with an in-depth knowledge of taxation likely do not know how to manually create XML files. But a significant percentage of the population know how to use Microsoft Excel. And therein lies the key to solving the MeF testing problem. But, why stop with MeF? Almost any XML files can be created using a similar approach.

## A little disclaimer
Earlier versions of TestDataCreator are being used successfully by *two* State Revenue agencies for testing MeF systems. However, the open-source version available on GitHub is still an 'work in progress'. (Code from earlier versions is gradually being refactored and improved for the open-source version.) Though the open-source version is far from complete, it might be helpful to get a sense for where this project is headed. With that goal in mind, the samples below were generated using *earlier* implementations of the tool. Ultimately, though, the open-source version will support everything the earlier versions support, and much more.

## I'm bored, details please
TestDataCreator is a tool that uses XML schemas (XSD) and Microsoft Excel to support the creation and validation of XML test files. It is written in Java, and makes use of various libraries, including Apache Xerces (for XML schema processing) and Apache POI (for Excel file processing).

## Hmmm, that doesn't help much. How would it be used?
TestDataCreator would typically be used as follows:

- Start with a set of XML schema files.
- Determine the 'root' element within the schemas for each type of XML document to be created.
- Configure a 'model' based on each 'root' element. 
  - A 'model' is essentially a specific structural representation (think: tree) beginning with a particular 'root' element. The configuration may include, for example, the maximum number of each repeating element, for elements where 'maxOccurs' is greater than 1.
- Using TestDataCreator, generate a 'book' file (essentially an Excel workbook) containing one or more 'pages' (essentially a worksheet within that workbook). 
  - The left-most columns of each 'page' will contain a structural representation (a tree) of the 'model' defined for that 'page'. 
- Once generated, a 'book' file can then be provided to non-technical users/testers as the starting point for their testing.
  - Users can then create 'test cases' by filling in values within columns on the 'pages' of the Excel 'book' file.
  - Each column on a 'page' represent a 'test document', which will ultimately be generated as an XML file. Each 'test document' conforms to the 'model' defined for its particular 'page'. 
  - A 'test case' can be made up of one or more 'test documents' (XML files) conforming to one or more different 'models'. 
  - A 'book' file can contain one or more 'test cases'.
- Once a user has created a 'book' file containing 'test cases', they can schema validate the 'test cases' using TestDataCreator. 
  - If any errors are discovered during schema validation, these errors will be written to a 'Log' worksheet in the Excel 'book' file, along with hyperlinks to the locations of each error within each 'test document'. (This helps the users to easily locate and correct errors.)
- Once a user is comfortable that the 'test cases' in a 'book' are ready for testing, they can use TestDataCreator to generate 'test case' XML files, which can then be loaded into the system to be tested.
- Wash, rinse, repeat.

## I'm a visual learner. Show me!
Sure, why not. Here are a few sample screenshots (with comments) ...

![screenshot1](https://cloud.githubusercontent.com/assets/16735709/13554422/742b89f0-e375-11e5-9f1a-1164e6ebb730.JPG)
- What we have here is a sample of a TestDataCreator 'book' containing two 'pages' ('1040A' and 'Manifest') and a 'Log' worksheet. This particular sample shows the 'page' for the Federal 1040A form.
- On the left (beginning at row 5) is the start of a Federal 1040A form, represented visually, with levels indented to show the hierarchy of XML elements.
- 'Test cases' are shown beginning in column Y. (In this example there are two test cases.)
- Additional information, such as form line number, data type, notes, and occurrence is shown beginning in column U. (To save space, the text in some of these cells is 'shrunk'; to view, the user simply needs to click on each cell.)
- The 'Occurs' column (X) represents the 'minOccurs' and 'maxOccurs' for each element. (For example, '0..1' indicates minOccurs=0, maxOccurs=1, and '3..n' indicates minOccurs=3, maxOccurs=unbounded.)
- Non-leaf/parent elements are shown in blue. Attributes are shown in green, prefixed with '@'.

___

![screenshot2](https://cloud.githubusercontent.com/assets/16735709/13554421/742b23b6-e375-11e5-9d4a-0fbc1f0ba2ee.JPG)
- In the sample above, notice the '[CHOICE]' marker on line 52. This indicates the start of a 'choice' within the XML. 
- The red '>' markers to the left of 'USAddress' and 'ForeignAddress' indicate each of the possible choices allowed by the schema. A user may only populate values for at most *one* of the choices (otherwise the resulting XML will fail schema validation).

___

![screenshot3](https://cloud.githubusercontent.com/assets/16735709/13554425/7430e10c-e375-11e5-8c57-2c4284f1ef8b.JPG)
- In the sample above, notice the '1' and '2' on the left? The schema definition allows the 'DependentDetail' element (lines 176, 186, 196) to be repeated. (It has 'Occurs' defined as '0..100', meaning minOccurs=0, maxOccurs=100.)
  - In 'test case' 1 (column Y), two dependents are specified (Sally and Josiah). 
  - In 'test case' 2 (column Z), only one dependent is specified (Jackson).
- Also notice the 'choice' in each repeating group. Any level of nested choices and repeating element groups is supported.

___

![screenshot4](https://cloud.githubusercontent.com/assets/16735709/13554423/742e96c2-e375-11e5-880f-ae7df3a551a7.JPG)
- In the sample above, notice the '1 | 1' and '1 | 2' on the left? This indicates that the 'W2StateLocalTaxGrp' element repeats (twice in the image), but that it is *also* part of a larger group of elements that repeats. Any level of repeating element groups is supported.

___

![screenshot5](https://cloud.githubusercontent.com/assets/16735709/13554424/7430390a-e375-11e5-9dba-b3f9e99a9962.JPG)
- The sample above shows another 'page' ('Manifest') in the same Excel 'book' file. 
  - When testing an MeF system, every 'test case' must contain a manifest file in addition to a tax return file (in this case, a 1040A). But this is completely configurable based on the needs of the system being tested. Some systems may require multiple 'pages' per 'test case'; some only one.
- Notice that 'test cases' 1 and 2 are also represented on this 'page. This shows how a 'test case' can be made up of any number of 'test documents' on any number of 'pages'.

___

![screenshot6](https://cloud.githubusercontent.com/assets/16735709/13554426/74387e6c-e375-11e5-99bc-5abe90a53965.JPG)
- The sample above shows the 'Log' worksheet. 
- Notice that there are numerous schemas-validation errors shown. These will need to be resolved by a user before valid XML files can be generated.
- The '<-----' markers are hyperlinks that, when clicked, will take the user to the location of each error.

___

If you're interested in playing around with the Excel 'book' file, click here: [2015_IRS-1040_Demo.xlsx](https://github.com/gbtorrance/TestDataCreator/files/160333/2015_IRS-1040_Demo.xlsx)

## That's it for now
Though the open-source version of TestDataCreator is a 'work in progress', hopefully the above will provide a sense for what to expect of the project in future. Watch this space.  