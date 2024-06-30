package com.finalpos.POSsystem.Controller;

import com.finalpos.POSsystem.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.finalpos.POSsystem.Model.Package;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@ResponseBody
@RequestMapping("/api/orders")
public class OrdersController {
    @Autowired
    OrderRepository orderDb;
    @Autowired
    CustomerRepository customerDb;
    @Autowired
    UserRepository userDb;
    @Autowired
    OrderDetailModelRepository orderDetailDb;


    @GetMapping("/{order_number}") // Đạt
    private Package getOrderByOrderNumber(@PathVariable("order_number") String order_number){
        try {
            OrderModel orderDB = orderDb.findByOrderNumber(order_number);
            if (orderDB != null) {

                String customer_id = orderDB.getCustomerId();
                String staff_id = orderDB.getStaffId();

                CustomerModel customerDB = customerDb.findCustomerById(customer_id);
                UserModel staffDB = userDb.findUserModelById(staff_id);
                OrderDetailModel order_detailDB  = orderDetailDb.findByOrderNumber(order_number);

                Object data = new Object() {
                    public final OrderModel order = orderDB;
                    public final CustomerModel customer = customerDB;
                    public final UserModel staff = staffDB;
                    public final OrderDetailModel orderDetail = order_detailDB;
                };

                return new Package(0, "Order exist", data);

            } else
                return new Package(404, "Order not exist", null);
        }catch (Exception e){
            return new Package(404, e.getMessage(), null);
        }
    }

    @GetMapping("/") // Đạt
    private Package getAllOrders(@RequestParam Optional<String> page){
        try {
            int pageSize = 10;
            int pageNumber = 1;
            if(!page.isEmpty() && page.get() != "null") {
                pageNumber = Integer.parseInt(page.get());
            }
            int skipAmount = (pageNumber - 1) * pageSize;
            int totalOrders = (int) orderDb.count();
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);


            List<OrderModel> orderList = orderDb.findAll();
            List<OrderModel> order = new ArrayList<>();

            // Check num of users in the last page
            // It will continue() when page + 1 (skipAmount > size()) -> reduce run time
            int endIdx = Math.min(skipAmount + pageSize, orderList.size());
            for (int i = skipAmount; i < endIdx; i++) {
                order.add(orderList.get(i));
            }

            Object data = new Object() {
                public final List<OrderModel> orders = order;
                public final int divider = totalPages;
            };
            return new Package(0, "success", data);

        }catch (Exception e){
            return new Package(404, e.getMessage(), null);
        }
    }
}