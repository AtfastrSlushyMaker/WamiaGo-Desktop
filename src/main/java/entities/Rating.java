package entities;

public class Rating {
    private int idRating;
    private int userId;
    private int driverId;
    private String comment;
    private int rating;

    public Rating() {
        this.idRating = 0;
        this.userId = 0;
        this.driverId = 0;
        this.comment = "";
        this.rating = 0;
    }

    public Rating(int idRating, int userId, int driverId, String comment, int rating) {
        this.idRating = idRating;
        this.userId = userId;
        this.driverId = driverId;
        this.comment = comment;
        this.rating = rating;
    }



    public int getIdRating() {
        return idRating;
    }

    public void setIdRating(int idRating) {
        this.idRating = idRating;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "idRating=" + idRating +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                '}';
    }
}
