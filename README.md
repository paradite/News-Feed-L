Multi-source News Feed App to test out new features in Android L
===

Features:
---
+ Search for news feeds across different sources using keyword
+ Sources supported: Twitter, Google+
+ Sources in development: Facebook, etc

Techniques used:
---
### RecyclerView
+ Basic layout with image and text for each item
+ Dynamically update the views with new data from different sources
+ Handle click events and start new activity
+ Remove item dynamically

### CardView
+ Used as container for each item in RecyclerView
+ Dynamically added to show details of each tweet when user tap on the tweet.

### Twitter Integration
+ Twitter API(through twitter4j) to fetch public tweets
+ Two separate async tasks to fetch tweets and profile pictures

### Google+ Integration
+ Google+ API(through Google HTTP Client Library for Java) to fetch public posts on Google+
