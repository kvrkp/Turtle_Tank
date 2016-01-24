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
        snacksCount = (snacksCount != 5) ? snacksCount++ : 0;

        String[] snacks = {"#Loveit when I get some #attention!\nThanks for the food!",
                "Sure is #awesome when that food falls from the sky!",
                "#NomNomNom...",
                "#Glorious day! #foodporn",
                "You've got to tell me this turtle pellet #recipe, it's the best!",
                "#Yummy! #PrayForPellets"};

        String message = snacks[snacksCount] + "\n\n#turtle #IoT #RaspberryPi goo.gl/U1kYa7";

        twitter.updateStatus(message);
    }
}