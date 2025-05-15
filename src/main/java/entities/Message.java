package entities;




import java.time.LocalDateTime;


public class Message {
    
    private Long id;

 
    private String fromEmail;

  
    private String toEmail;

 
    private String content;

  
    private LocalDateTime timestamp;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFromEmail() {
		return fromEmail;
	}


	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}


	public String getToEmail() {
		return toEmail;
	}


	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public LocalDateTime getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}


	public Message(Long id, String fromEmail, String toEmail, String content, LocalDateTime timestamp) {
		super();
		this.id = id;
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.content = content;
		this.timestamp = timestamp;
	}


	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    
    
}