import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

public class Branch {
    String city;
    String district;
    String id;
    String lastMonth = "";
    MyHashMap<String, Employee> employeeHashMap = new MyHashMap<>(101);
    Queue<Employee> cooksWaitingForPromotion = new LinkedList<>();
    Queue<Employee> cashiersWaitingForPromotion = new LinkedList<>();
    Queue<Employee> cashiersWaitingForLeaving = new LinkedList<>();
    Queue<Employee> cooksWaitingForLeaving = new LinkedList<>();
    Queue<Employee> couriersWaitingForLeaving = new LinkedList<>();
    boolean managerWaitingForLeaving = false;

    Employee manager;
    int numOfCooks = 0;
    int numOfCouriers = 0;
    int numOfCashiers = 0;
    int monthlyTotalBonus = 0;
    int allTimeBonus = 0;
    FileWriter fileWriter;

    Branch(String city, String district, FileWriter fileWriter) {
        this.fileWriter = fileWriter;
        this.city = city;
        this.district = district;
        this.id = city + district;
    }

    public void addEmployee(String name, String role) throws Exception {
        Employee newEmployee = new Employee(name, role);

        if (employeeHashMap.contains(name)) {
            fileWriter.write("Existing employee cannot be added again.\n");
            return;
        }

        //COURIER
        if (role.equals("COURIER")){
            employeeHashMap.insert(name, newEmployee);
            numOfCouriers++;

            //checking if there are any couriers to be dismissed
            if(!couriersWaitingForLeaving.isEmpty()){
                Employee courierToBeRemoved = couriersWaitingForLeaving.poll();
                employeeHashMap.remove(courierToBeRemoved.name);
                numOfCouriers--;
                fileWriter.write(courierToBeRemoved.name + " is dismissed from branch: " + district + ".\n");
            }
        }

        //CASHIER
        else if(role.equals("CASHIER")){
            employeeHashMap.insert(name, newEmployee);
            numOfCashiers++;

            //checking if there are any cashiers to be dismissed
            if(!cashiersWaitingForLeaving.isEmpty()){
                Employee cashierToBeRemoved = cashiersWaitingForLeaving.poll();
                employeeHashMap.remove(cashierToBeRemoved.name);
                numOfCashiers--;
                fileWriter.write(cashierToBeRemoved.name + " is dismissed from branch: " + district + ".\n");
            }

            //checking if there are any cashiers waiting for promotion
            if(!cashiersWaitingForPromotion.isEmpty()){
                Employee cashierToBePromoted = cashiersWaitingForPromotion.poll();
                cashierToBePromoted.role = "COOK";
                cashierToBePromoted.promotionPoint -= 3;
                numOfCashiers--;
                numOfCooks++;
                fileWriter.write(cashierToBePromoted.name + " is promoted from Cashier to Cook.\n");

                //double promotion
                if (cashierToBePromoted.promotionPoint >= 10) {
                    cooksWaitingForPromotion.add(cashierToBePromoted);
                }

                //after a new cook is added, dismissals and promotions are possible for cooks
                //promotions
                if (managerWaitingForLeaving) {
                    if (!cooksWaitingForPromotion.isEmpty() && numOfCooks > 1) {
                        //get the new manager and remove it from the queue
                        Employee newManager = cooksWaitingForPromotion.poll();
                        newManager.role = "MANAGER";
                        newManager.promotionPoint -= 10;
                        numOfCooks--;

                        //deleting the old manager
                        employeeHashMap.remove(manager.name);

                        managerWaitingForLeaving = false;

                        fileWriter.write(manager.name + " is dismissed from branch: " + district + ".\n");
                        fileWriter.write(newManager.name + " is promoted from Cook to Manager.\n");
                        this.manager = newManager;
                    }
                }

                //dismissals
                if(!cooksWaitingForLeaving.isEmpty() && numOfCooks > 1){
                    Employee cookToBeDismissed = cooksWaitingForLeaving.poll();
                    numOfCooks--;
                    employeeHashMap.remove(cookToBeDismissed.name);
                    fileWriter.write(cookToBeDismissed.name + " is dismissed from branch: " + district + ".\n");
                }

            }
        }
        //COOK
        else if(role.equals("COOK")) {
            employeeHashMap.insert(name, newEmployee);
            numOfCooks++;

            //checking if there are any cooks to be dismissed
            if(!cooksWaitingForLeaving.isEmpty()){
                Employee cookToBeRemoved = cooksWaitingForLeaving.poll();
                employeeHashMap.remove(cookToBeRemoved.name);
                numOfCooks--;
                fileWriter.write(cookToBeRemoved.name + " is dismissed from branch: " + district + ".\n");
            }

            //checking if there are any cooks waiting for promotion
            if(!cooksWaitingForPromotion.isEmpty() && managerWaitingForLeaving) {
                //get the new manager and remove it from the queue
                Employee newManager = cooksWaitingForPromotion.poll();
                newManager.role = "MANAGER";
                newManager.promotionPoint -= 10;
                numOfCooks--;

                //deleting the old manager
                employeeHashMap.remove(manager.name);
                managerWaitingForLeaving = false;


                fileWriter.write(manager + " is dismissed from branch: " + district + ".\n");
                fileWriter.write(newManager.name + " is promoted from Cook to Manager.\n");
                this.manager = newManager;
            }

        }
        //MANAGER
        else if(role.equals("MANAGER")) {
            this.manager = newEmployee;
            employeeHashMap.insert(name, newEmployee);
        }
    }

    public void update(String employeeName, int score, String month) throws Exception{
        Employee employee = employeeHashMap.get(employeeName);

        if (!month.equals(lastMonth)) {
            lastMonth = month;
            monthlyTotalBonus = 0;
        }

        if (employee == null) {
            fileWriter.write("There is no such employee.\n");
            return;
        }


        int oldPromotionPoint = employee.promotionPoint;
        employee.promotionPoint += score / 200;
        employee.bonus = score > 0 ? score % 200 : 0;

        monthlyTotalBonus += employee.bonus;
        allTimeBonus += employee.bonus;

        //checking potential promotions and dismissals

        //COURIER
        if (employee.role.equals("COURIER")) {
            if (employee.promotionPoint <= -5 && oldPromotionPoint > -5) {
                //dismiss directly
                employeeHashMap.remove(employeeName);
                numOfCouriers--;
                fileWriter.write(employeeName + " is dismissed from branch: " + district + ".\n");
            } else if (employee.promotionPoint > -5 && oldPromotionPoint > -5) {
                couriersWaitingForLeaving.remove(employee);
            }
        }

        //CASHIER
        if (employee.role.equals("CASHIER")) {
            if (employee.promotionPoint >= 3 && oldPromotionPoint < 3) {
                if (numOfCashiers > 1) {
                    //promote directly
                    employee.role = "COOK";
                    fileWriter.write(employeeName + " is promoted from Cashier to Cook.\n");
                    numOfCashiers--;
                    numOfCooks++;
                    employee.promotionPoint -= 3;

                    //insertion of a new cook may trigger some promotions and dismissals of cooks
                    //promotions
                    if (managerWaitingForLeaving) {
                        if (!cooksWaitingForPromotion.isEmpty() && numOfCooks > 1) {
                            //get the new manager and remove it from the queue
                            Employee newManager = cooksWaitingForPromotion.poll();
                            newManager.role = "MANAGER";
                            newManager.promotionPoint -= 10;
                            numOfCooks--;

                            //deleting the old manager
                            employeeHashMap.remove(manager.name);

                            managerWaitingForLeaving = false;

                            fileWriter.write(manager.name + " is dismissed from branch: " + district + ".\n");
                            fileWriter.write(newManager.name + " is promoted from Cook to Manager.\n");
                            this.manager = newManager;
                        }
                    }

                    //dismissals
                    if(!cooksWaitingForLeaving.isEmpty() && numOfCooks > 1){
                        Employee cookToBeDismissed = cooksWaitingForLeaving.poll();
                        numOfCooks--;
                        employeeHashMap.remove(cookToBeDismissed.name);
                        fileWriter.write(cookToBeDismissed.name + " is dismissed from branch: " + district + ".\n");
                    }

                } else {
                    //add to the promotion queue
                    cashiersWaitingForPromotion.add(employee);
                }
            } else if (employee.promotionPoint <= -5 && oldPromotionPoint > -5) {
                if (numOfCashiers > 1) {
                    //dismiss directly
                    fileWriter.write(employeeName + " is dismissed from branch: " + district + ".\n");
                    employeeHashMap.remove(employeeName);
                    numOfCashiers--;
                } else {
                    //add to the dismissal queue
                    cashiersWaitingForLeaving.add(employee);
                }
            } else if (employee.promotionPoint > -5 && oldPromotionPoint <= -5) {
                //remove from dismissal queue
                cashiersWaitingForLeaving.remove(employee);
            } else if (employee.promotionPoint < 3 && oldPromotionPoint >= 3) {
                //remove from promotion queue
                cashiersWaitingForPromotion.remove(employee);
            }

        }

        //COOK
        if (employee.role.equals("COOK")) {
            if (employee.promotionPoint >= 10 && !cooksWaitingForPromotion.contains(employee)) {

                //add to the promotion queue
                cooksWaitingForPromotion.add(employee);

                //if the manager mey be already waiting for leaving
                if(managerWaitingForLeaving){
                    if (!cooksWaitingForPromotion.isEmpty() && numOfCooks > 1) {
                        //get the new manager and remove it from the queue
                        Employee newManager = cooksWaitingForPromotion.poll();
                        newManager.role = "MANAGER";
                        newManager.promotionPoint -= 10;
                        numOfCooks--;

                        //deleting the old manager
                        employeeHashMap.remove(manager.name);

                        managerWaitingForLeaving = false;

                        fileWriter.write(manager.name + " is dismissed from branch: " + district + ".\n");
                        fileWriter.write(newManager.name + " is promoted from Cook to Manager.\n");
                        this.manager = newManager;
                    }
                }

            } else if (employee.promotionPoint <= -5 && oldPromotionPoint > -5) {
                if (numOfCooks > 1) {
                    //dismiss directly
                    employeeHashMap.remove(employeeName);
                    fileWriter.write(employeeName + " is dismissed from branch: " + district + ".\n");
                    numOfCooks--;
                } else {
                    //add to the dismissal queue
                    cooksWaitingForLeaving.add(employee);
                }
            } else if (employee.promotionPoint > -5 && oldPromotionPoint <= -5) {
                //remove from dismissal queue
                cooksWaitingForLeaving.remove(employee);
            } else if (employee.promotionPoint < 10 && oldPromotionPoint >= 10) {

                //remove from promotion queue
                cooksWaitingForPromotion.remove(employee);
            }
        }

        //MANAGER
        if (employee.role.equals("MANAGER")) {
            if (employee.promotionPoint <= -5) {
                managerWaitingForLeaving = true;

                //dismiss
                if (!cooksWaitingForPromotion.isEmpty() && numOfCooks > 1) {
                    //get the new manager and remove it from the queue
                    Employee newManager = cooksWaitingForPromotion.poll();
                    newManager.role = "MANAGER";
                    newManager.promotionPoint -= 10;
                    numOfCooks--;
                    this.manager = newManager;

                    //deleting the old manager
                    employeeHashMap.remove(employeeName);

                    managerWaitingForLeaving = false;

                    fileWriter.write(employee.name + " is dismissed from branch: " + district + ".\n");
                    fileWriter.write(newManager.name + " is promoted from Cook to Manager.\n");
                }
            } else {
                managerWaitingForLeaving = false;
            }
        }
    }

    public void leave(String employeeName, String month) throws Exception{
        Employee employee = employeeHashMap.get(employeeName);

        if (!month.equals(lastMonth)) {
            lastMonth = month;
            monthlyTotalBonus = 0;
        }

        if (employee == null) {
            fileWriter.write("There is no such employee.\n");
            return;
        }

        //CASHIER
        if (employee.role.equals("CASHIER")) {
            if (employee.promotionPoint > -5)
                if (numOfCashiers > 1) {
                    //remove the employee
                    employeeHashMap.remove(employeeName);
                    cashiersWaitingForPromotion.remove(employee);
                    numOfCashiers--;
                    fileWriter.write(employee.name + " is leaving from branch: " + district + ".\n");
                } else {
                    employee.bonus += 200;
                    allTimeBonus += 200;
                    monthlyTotalBonus += 200;
                }

        }
        //COURIER
        else if (employee.role.equals("COURIER")) {
            if (employee.promotionPoint > -5)
                if (numOfCouriers > 1) {
                    //remove the employee
                    employeeHashMap.remove(employeeName);
                    numOfCouriers--;
                    fileWriter.write(employee.name + " is leaving from branch: " + district + ".\n");
                } else {
                    employee.bonus += 200;
                    allTimeBonus += 200;
                    monthlyTotalBonus += 200;
                }
        }
        //COOK
        else if (employee.role.equals("COOK")) {
            if (employee.promotionPoint > -5)
                if (numOfCooks > 1) {
                    //remove the employee
                    employeeHashMap.remove(employeeName);
                    cooksWaitingForPromotion.remove(employee);
                    numOfCooks--;
                    fileWriter.write(employee.name + " is leaving from branch: " + district + ".\n");
                } else {
                    employee.bonus += 200;
                    allTimeBonus += 200;
                    monthlyTotalBonus += 200;
                }
        }
        //MANAGER
        else if (employee.role.equals("MANAGER")) {
            if (employee.promotionPoint > -5) {
                //dismissal
                if (!cooksWaitingForPromotion.isEmpty() && numOfCooks > 1) {
                    //get the new manager and remove it from the queue
                    Employee newManager = cooksWaitingForPromotion.poll();
                    newManager.role = "MANAGER";
                    newManager.promotionPoint -= 10;
                    numOfCooks--;
                    this.manager = newManager;

                    //deleting the old manager
                    employeeHashMap.remove(employeeName);

                    fileWriter.write(employee.name + " is leaving from branch: " + district + ".\n");
                    fileWriter.write(newManager.name + " is promoted from Cook to Manager.\n");

                }
                //failed give money
                else {
                    employee.bonus += 200;
                    allTimeBonus += 200;
                    monthlyTotalBonus += 200;
                }
            }
        }
    }

    public void printManager() throws Exception{
        fileWriter.write("Manager of the " + district + " branch is " + manager.name + ".\n");
    }

    public void printMonthlyBonus(String month) throws Exception{
        if (!lastMonth.equals(month)) monthlyTotalBonus = 0;
        fileWriter.write("Total bonuses for the " + district + " branch this month are: " + monthlyTotalBonus + "\n");
    }

    public void printAllTimeBonus() throws Exception{
        fileWriter.write("Total bonuses for the " + district + " branch are: " + allTimeBonus + "\n");
    }
}