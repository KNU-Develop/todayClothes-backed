package org.project.todayclothes.dto.gpt;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClotheRecommendPromptDto {
    // Weather Information
    private Double temperature;
    private Double feelsLike;
    private Double rainProbability;
    private Double humidity;
    private Double windSpeed;

    // Schedule Information
    private String activityTime;
    private String location;
    private String area;
    private String dressCode;

    @Override
    public String toString() {
        return "You are an API server designed to recommend outfits based on weather and schedule information. Use the provided clothes list, which serves as a vector database, to choose suitable clothing for each category (TOP, BOTTOM, ACCESSORY, SHOES, OUTERWEAR). Provide one item per category along with a brief one-line comment on today’s outfit.\n" +
                "\n" +
                "**Weather Information:**\n" +
                "- Temperature: " + this.temperature + "°C\n" +
                "- Feels Like: " + this.feelsLike + "°C\n" +
                "- Rain Probability: " + this.rainProbability+ "%\n" +
                "- Humidity: " + this.humidity + "%\n" +
                "- Wind Speed: " + this.windSpeed + "km/h\n" +
                "\n" +
                "**Schedule Information:**\n" +
                "- Activity Time: " + this.activityTime + "\n" +
                "- Location: " + this.location + "\n" +
                "- Area: " + this.area + "\n" +
                "- Dress Code: " + this.dressCode +"\n" +
                "\n" +
                "**Output Format:**\n" +
                "{ \"top\": UUID, \"bottom\": UUID, \"accessory\": UUID, \"shoes\": UUID, \"outerwear\": UUID, \"message\": string }\n" +
                "\n" +
                "Choose items from the following clothes list and ensure each category is represented in your response\n";
    }
}
