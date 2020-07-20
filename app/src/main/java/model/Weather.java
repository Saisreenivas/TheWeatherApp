package model;

/**
 * Created by Sai sreenivas on 2/20/2017.
 */

public class Weather {

    public Place place;
    public String iconData;
    public CurrentCondition currentCondition =  new CurrentCondition();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();

}
