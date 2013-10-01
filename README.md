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

* page impression (user accessed any page)
* visitor
** known visitor
** unknown visitor