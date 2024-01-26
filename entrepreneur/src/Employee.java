public class Employee {
    String name;
    String role;
    int monthlyScore;
    int promotionPoint;
    int bonus;

    Employee(String name, String role){
        this.name = name;
        this.role = role;
        promotionPoint = 0;
        monthlyScore = 0;
        bonus = 0;
    }

}
