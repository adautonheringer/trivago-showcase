# Trivago Showcase
This is a showcase app for discussing with the Trivago engineering team. 
I decided to take the map part of Trivago App and made some small tweaks to improve user experience. 
The changes are not necessarily better, but it is going to give us a lot of material to discuss.
The left one is my Showcase app and the right is the Trivago app. I used the [Yelp API](https://www.yelp.com/developers/documentation/v3/business) to mimic the Trivago API.

![image](https://user-images.githubusercontent.com/30607119/169636086-c3e921ed-9012-484d-a48c-73202c027978.png)
![image](https://user-images.githubusercontent.com/30607119/169636269-891e7547-41c5-4cb9-88ea-7a5743fd6022.png)

### Main changes
#### Toolbar and Transparent Status Bar
I've made the toolbar floating and make statusBar transparent. 
This give users the feeling that the screen is bigger, and that's why this strategy is used by other map-oriented apps such as googleMaps and Uber. 

#### Padding in GoogleMaps and Navigation Bar transparency
At the bottom, I've put the google maps log above the list. With this, the list gets down, closer to users thumbs.

#### Animations
Most important improvements are the animations (I strongly encourage you to compile the project). Over the map, instead of using markers, 
I used GoogleMap projections and cameraMoveListeners to bind android views to map. The advantage of this strategy is animations since 
plain markers cannot be animated.
Also, and more importante, I used sharedElementTransition when list item is clicked and we go to details fragment. 
That is a great choreography since users follow the story of the app better with purposefully motion. 
And that was my goal: not just using animation, but improve the choreography of the app, making motion guide the user. I liked the result.  

# Compilation
To compile the project, just download it and click the play button in android-studio, preferably API level 31. There are two API_KEYS in the project: 
for googleMaps and Yelp. I've decided to upload them to make it easy for you to compile. So don't worry, after our conversation I'll change the keys.

# Architecture
I used MVVM and clean architecture with unidirectional data flow: 
## views -> viewModel -> repository -> dataSource 
(-> means dependency, so it is important that lower level code never depend on higher levels). 
Also, I organize the code with package by features, so it is easy to navigate the files. 
To dependency injection I use Hilt for its simplicity over Dagger. 
I did not customize the theme correctly, but it is definetely important for future improvements.
Instead of using Livedata, I used stateFlow along with Kotlin coroutines for pure Kotlin usage. 
A word on Jetpack Compose, I used it for the searchFragment, and the reason is to show they are great for interoperability.
With all this, this architecture is highly scalable because of its simplicity and straightforwardness.

### 1. Views
In terms of the app itself, there are four fragments held by the same MainActivity, and they all share the same viewModel (MainViewModel). 
The four fragments are:
1. MapFragment -> responsible for managing the Map and the views over it.
2. SearchFragment -> responsible for the refresh button that appears when user change location of the map.
3. BusinessesFragmens -> holds the horizontal list of business fetched by Yelp API.
4. DetailsFragment -> is the screen with the details of that business.

### 2. ViewModel 
The ViewModel contains 5 stateFlows, one for each view. 
These stateFlows emit a state modeled with data class for each view, so each view observe only what they need to.
Also, the ViewModel contain all the actions (use cases) the user can perform. 
I could have modeled states with sealed classes, but in this case data class was preferable for its simplicity.

### 3. Repository
The MainRepository basically wraps the two http request our app perform with Yelp API: getBusinesses and getBusinessDetails.

### 4. YelpAPI Data Source
The API layer is quite straightforward: retrofit and okhttp to make the request; 
dataclasses to parse the response; and mappers to map responses to objects more usables.

# Tests
Testing coroutines and stateFlows are a bit tricky, but we manage to do it. We use pure JUnit and runTest. 
Basically the tests are made so we perform an action and observe the chain of states emitted by the viewModel. 
For example, when getBusinesses is called, the state emit isLoading immediately, and only after receiving the response it emits the businesses list.
So we focused in testing this chain of events.
Since in our architecture the ViewModel holds all the logical heavy weight, I prioritize testing it. 
I could have tested the respository in the same way as well, but basically I ran out of time. 
Also, instrumented tests are important especially to check compatibility with other devices, but I decided not to implement it for simplicity.

# Final
I was great building this app and I hope you compile and run it on your devices/emulators. 
I quite liked the result and I am looking forward to hear your thoughts on my decisions, both in terms of architecture as well as UX.
Let me know. Thank you!
