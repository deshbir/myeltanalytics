package myeltanalytics;

public enum Status
{
    SUCCESS(1),FAILURE(0), WAITING(2);
    private int statusCode;
    
    Status(int code){
       this.statusCode = code; 
    }
    
    int getStatusCode(){
      return statusCode;   
    }
}
