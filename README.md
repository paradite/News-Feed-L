App to search for keywords across different social media sites
===

Final decision on App name:
---
+ Whatsoever

Links:
---
+ <a href="https://play.google.com/store/apps/details?id=thack.ac.whatsoever" target="_blank">Play Store listing</a>

App name candidates:
---
+ No Login (as the app only uses publicly accessible feeds)
+ Social Monitor (the app monitors social media based on keywords)
+ What's going on (real time keyword tracking)
+ happening
+ SoSe - Social Search
+ Whatever
+ Feed
+ Track
+ People say - What the people say

Features:
---
+ Real-time keyword tracking on social media sites
+ Completely anonymous (no log-in required)
+ Social media supported: Twitter, Google+, Instagram
+ Social media not yet supported: Facebook, etc

Techniques used:
---
### RecyclerView
+ Basic layout with image and text for each item
+ Dynamically update the views with new data from different sources
+ Handle click events and start new activity
+ Remove item dynamically

### CardView
+ Used as container for each item in RecyclerView
+ Allow user to interact with the CardView by providing onClick methods and buttons
+ Dynamically added to show details of each tweet when user tap on the tweet.

### Twitter Integration
+ Twitter API(through twitter4j) to fetch public tweets
+ Two separate async tasks to fetch tweets and profile pictures

### Google+ Integration
+ Google+ API(through Google HTTP Client Library for Java) to fetch public posts on Google+

### Instagram Integration
+ Instagram API to fetch photos using queried terms on Instagram
