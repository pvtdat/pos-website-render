import Num2VND from "../Num2VND";
import {Link} from 'react-router-dom';
function OrderItem({index, item}) {
    return ( 
        <tr>
            <th scope="row">{index}</th>
            <td>{item.orderNumber}</td>
            <td>{Num2VND(item.total)}</td>
            <td>{item.quantity}</td>
            <td>{new Date(item.created_date).toDateString()}</td>
            <td>
                <Link to={"/orders/"+item.orderNumber} type="button" className="btn btn-outline-main btn-sm m-1">
                    <i className="fa-solid fa-circle-info mr-2"></i>
                    Details
                </Link>
            </td>
        </tr>
    );
}

export default OrderItem;