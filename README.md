basar-track
===========

This package provides a very, very, very ... very simple tracking component to be used inside any application for tracking
an arbitrary number of event types. These may include site access, user interaction, in-app events (iOS, Android, unameit).

Actually, the key idea behind this sub-project is to provide (1) a learning ground, (2) a sandbox and (3) content to be used
for an upcoming conference talk about the use of asynchronous architectures in ecommerce applications.

As web tracking seems to be a very basic, simple-to-understand use case it is great for showing concepts and ideas which are
later extended into any type of process implementation

The tracking itself is quite simple. The application provides converters for all available interfaces and converts inbound message 
to an abstract event entity. The tracking event entity requires only a minimum number of fixed attributes and allows an arbitrary
number of additional attributes. The interface and processing pipeline do not care about the content provided.

As the tracking interface(s) define no or only a few requirements on how event data must look like, it is up to the visualization
expert to define KPIs from the tracked metrics. Thus, the further specification must be done after data ingestion.

