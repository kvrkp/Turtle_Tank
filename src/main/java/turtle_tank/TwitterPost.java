package turtle_tank;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterPost {

    private static Twitter twitter = TwitterFactory.getSingleton();
    private static int snacksCount = 0;

    static public void postTodaysRoutine() throws TwitterException {
        String message = "Today's Routine:\n\nSunrise: " + SunRSDB.getSunrise() +
                                         "\nBreakfast: " + SunRSDB.getBreakfast() +
                                         "\nDinner: " + SunRSDB.getDinner() +
                                         "\nSunset: " + SunRSDB.getSunset() +
                                         "\n\n#turtle #IoT #RaspberryPi goo.gl/U1kYa7";
        twitter.updateStatus(message);
    }

    static public void postSnacks() throws TwitterException {
        snacksCount = (snacksCount != 9) ? snacksCount++ : 0;

        String[] snacks = {"#Loveit when I get some #attention!\nThanks 4 the food!",
                "Sure is #awesome when that food falls from the sky! Thanks",
                "#NomNomNom... Thanks 4 the food!",
                "#Glorious day! My kind of #FoodPorn Thank you!",
                "You've got to tell me this turtle pellet #recipe, it's the best!",
                "#Yummy! #PrayForPellets Thank you",
                "Give it to me! #SoGood! Thanks",
                "This isn't #pizza, but I will take it! Thanks!",
                "Good thing this shell hides my figure. Thanks 4 the food!",
                "#Enabler... #HappyTurtle #RedEaredSlider"};

        String message = snacks[snacksCount] + "\n\n#turtle #IoT #RaspberryPi goo.gl/U1kYa7";

        twitter.updateStatus(message);
    }
}