package turtle_tank;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TwitterPost {
    private static volatile Twitter twitter = TwitterFactory.getSingleton();
    private static volatile int snacksCount = 0;
    private static volatile String[] snacks = {"This isn't #pizza, but I will take it. Thanks.",
                                                "Good thing this shell hides my figure. Thanks 4 the food.",
                                                "#Enabler...",
                                                "#LoveIt when I get some #Attention. Thanks 4 the food.",
                                                "Sure is #Awesome when that food falls from the sky. Thanks.",
                                                "Excellent #NomNomNom... Thanks 4 the food.",
                                                "#Yummy #PrayForPellets Thank you.",
                                                "Give it to me #SoGood.",
                                                "You've got to tell me this turtle pellet #Recipe, it's the best.",
                                                "#Glorious day! My kind of #FoodPorn Thank you.",
                                                "Turtle in a #HalfShell, FEED ME NOW.",
                                                "All day I dream about pellets. Thanks."};

    private static volatile String[] hashTag = {"#IoT", "#Turtle", "#RedEaredSlider", "#Donnie", "#RaspberryPi",
                                                "#Automation", "#Java", "#Pi4j", "#Spring", "goo.gl/U1kYa7",
                                                "#HappyTurtle", "#TMNT", "#NinjaTurtles", "#Aquarium", "#Cowabunga",
                                                "#Shredder", "#Raphael", "#Mikey", "#Leo"};

    static public void postTodaysRoutine() throws TwitterException {
        String message = "Today's Routine:\n\nSunrise: " + SunRSDB.getSunrise() +
                                         "\nBreakfast: " + SunRSDB.getBreakfast() +
                                         "\nDinner: " + SunRSDB.getDinner() +
                                         "\nSunset: " + SunRSDB.getSunset() +
                                         "\n\n#turtle #IoT #RaspberryPi goo.gl/U1kYa7";
        twitter.updateStatus(message);
    }

    static public void postSnacks() throws TwitterException {
        StringBuilder message = new StringBuilder();
        Random random = new Random();
        Set<String> noDuplicates = new HashSet<>();

        message.append(snacks[snacksCount]);                                            // insert new message

        for (int x = 0; x < 3; ) {
            if(noDuplicates.add(hashTag[random.nextInt(hashTag.length)])) x++;          // create hash with no duplicates
        }

        for (String hash: noDuplicates) {                                               // append hashtags
            message.append(" ").append(hash);
        }

        twitter.updateStatus(message.toString());                                       // post to followers

        if (snacksCount == (snacks.length - 1)) snacksCount = 0;
        else snacksCount++;
    }
}