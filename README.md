basar-track
===========

This package provides a very, very, very ... very simple tracking component to be used inside any application for tracking
an arbitrary number of event types. These may include site access, user interaction, in-app events (iOS, Android, unameit).

Actually, the key idea behind this sub-project is to provide (1) a learning ground, (2) a sandbox and (3) content to be used
for an upcoming conference talk about the use of asynchronous architectures in ecommerce applications.

As web tracking seems to be a very basic, simple-to-understand use case it is great for showing concepts and ideas which are
later extended into any type of process implementation

The tracking itself is quite simple. Although the application is capable of handling any type of event with any number of
properties, it collects and aggregates the following data:

Plain and simple 

* url (http://www.yourdomain.com/path/to/destination)
* referer (http://www.otherdomain.com/another/path)
* (search) keywords (trousers summer shorts)
* source (google.com)

* operating system (iOS, Android, Windows)
* application (Internet Explorer, Chrome, your-ipad-app)
* language (en-en, de-de, ...)
* device type (tablet, desktop, sensor, ...)
* device (iPad, iPhone, Samsung S4, your-sensor, ...)
* javascript enabled (true, false)

* session identifier (123eeii3344dd)
* visitor (known and unknown) 
* age (35) 
* gender (male, female)
* domain identifier (yourdomain.com)
* subdomain identifier (yoursubdomain.com)

As the inbound interfaces are as flexible as possible providing an arbitrary number of attributes towards the processing units,
the list of metrics is by far complete.