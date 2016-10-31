## Analyze and Report California Appellate and Supreme Court Opinions

A Spring MVC application to read and analyze newly published California Appellate and Supreme court opinions.

#### This is a web based server application written as a technology and concept explorer. It reads court opinions, determines which statutes are referenced and computes aggregate counts. 
  * The statutes are organized by code section and statute titles and counts are displayed. 
  * Discovered statutes are linked to the "Guided Search" application so the statute text can be read.
  * The application allows self registration and sends weekly e-mails with new opinions.
  * Users can set preferences as to which California Codes are reported in weekly e-mails.
  * The application links to the original opinion document for quick reference.     

#### This application uses several essential Spring Frameworks
  * IoC Container & Dependency Injection.
  * WEB Model/View Controller & Validation, Data Binding, Type Conversion and Resources.
  * Data Access & Object Relational Mapping & Transaction Management.

#### The project is organized into several submodules
  * op - main application with web pages, controllers, services, repositories, etc. 
  * opinions submodule - database and application model for court opinions.
  * codesparser - multi-node linked list for holding statute hierarchy.
  * gsearch - code to search and manage statute hierarchy.
  * gscalifornia submodule - California specific search functions for loading and converting statutes.
  * opcalifornia - California specific opinion scraper code.  
  