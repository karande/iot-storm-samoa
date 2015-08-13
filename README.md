## Internet of Things demo using Storm, Samoa and Kafka

###Input Dataset: 

https://archive.ics.uci.edu/ml/datasets/PAMAP2+Physical+Activity+Monitoring

###Output:

Real Time activity prediction by simulating users producing real time data. 

Prequisite:

1) Setup SAMOA (Machine Learning component)

2) Setup Kafka (Real time data feeding to Kafka)

3) Setup Storm (Data processing framework)

####Step:1 
Input Data

Download dataset and copy data files into activity-dataset to simulate N users. 
```
  iot-storm-samoa
    /activity-dataset
      ---/task1
        ---subject101.dat
        ---subject102.dat
      ---/task2
      ---/task3
      ....
      ---/taskN
    /activity-storm-input
      ---/task1
      ---/task2
      ---/task3
      ....
      ---/taskN
```   
####Step:2 
Modify SAMOA performance evaluator class to print actual class and predicted class in following format
<user, actual class, predicted class>

####Step:3 
Create a PubNub account for messaging and add sub-key and pub-key into MessageHandler.java file

####Step:4 
Create a dashboard using Freeboard.io account to display user activities. 

https://www.youtube.com/watch?v=_hFpSQnPYXg

####Step:5 
Deploy Storm topology by running ActivityTopology.java which does preprocessing and passes data to Kafka. 

As soon as the data is ready into kafka SAMOA tasks reading from data is initiated and results are pushed to Freeborad.io dashboard using Pubnub messaging service. 

####Link for project presentation:
https://prezi.com/aase4u10vlsz/machine-learning-based-framework-for-building-iot-applications/






