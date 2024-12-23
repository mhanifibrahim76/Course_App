// Course section

package com.example.myapplication;

public class Course {
    private int courseId;
    private String title;
    private String content;
    private String imageUrl;
    private String youtubeUrl;

    public Course(int courseId, String title, String content, String imageUrl, String youtubeUrl) {
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.youtubeUrl = youtubeUrl;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }
}
