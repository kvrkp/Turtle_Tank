package turtle_tank;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterPost {
    private static volatile Twitter twitter = TwitterFactory.getSingleton();
    private static volatile int snacksCount = 0;
    private static volatile String[] snacks = {"This isn't #pizza, but I will take it!\nThanks!",
            "Good thing this shell hides my figure.\nThanks 4 the food!",
            "#Enabler... #HappyTurtle #RedEaredSlider",
            "#Loveit when I get some #attention!\nThanks 4 the food!",
            "Sure is #awesome when that food falls from the sky!\nThanks",
            "Excellent #NomNomNom...\nThanks 4 the food!",
            "#Yummy! #PrayForPellets\nThank you",
            "Give it to me! #SoGood!\n#Thanks",
            "You've got to tell me this turtle pellet #recipe, it's the best!",
            "#Glorious day! My kind of #FoodPorn\nThank you!",
            "Turtle in a #halfshell, FEED ME NOW! #Thanks!"};

    static public void postTodaysRoutine() throws TwitterException {
        String message = "Today's Routine:\n\nSunrise: " + SunRSDB.getSunrise() +
                                         "\nBreakfast: " + SunRSDB.getBreakfast() +
                                         "\nDinner: " + SunRSDB.getDinner() +
                                         "\nSunset: " + SunRSDB.getSunset() +
                                         "\n\n#turtle #IoT #RaspberryPi goo.gl/U1kYa7";
        twitter.updateStatus(message);
    }

    static public void postSnacks() throws TwitterException {
        String message = null;

        message = snacks[snacksCount] + "\n\n#turtle #IoT #RaspberryPi goo.gl/U1kYa7";

        twitter.updateStatus(message);

        if(snacksCount == (snacks.length - 1)) snacksCount = 0;
        else snacksCount++;
    }
}