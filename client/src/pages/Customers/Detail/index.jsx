import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import Profile from "./profile";
import Transactions from "./transactions";
function CustomerDetail() {
    const {id} = useParams();

    const [customer, setCustomer] = useState(null);
    const [error, setError] = useState(null);
    const server_url = process.env.REACT_APP_API_ENDPOINT;
    const url = `${server_url}/api/customers/${id}`;

    const handleFetchCustomer = async (req, res) => {
        axios.get(url, {
            headers: { 
                'Authorization': localStorage.getItem('token')
            }
        })
        .then(function (response) {
            const res = response.data;
            if(res.code === 0){
                setCustomer(res.data);
            }else{
                setError(response.message);
            }
        })
        .catch(function (error) {
            setError(error);
        });
    }

    useEffect(() => {
        handleFetchCustomer();
    }, []);
    return ( 
        <div className="card">
            <div className="card-body py-5">
                <div className="row">
                    <div className="col-sm-12 col-md-12 col-lg-3">
                        <ul className="nav nav-tabs list-group border-0">
                            <li className="nav-item">
                                <a className="nav-link active border-custom" data-toggle="tab" href="#profile">Profile</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link border-custom" data-toggle="tab" href="#transaction">Transaction History</a>
                            </li>
                        </ul>
                    </div>
                    <div className="col-sm-12 col-md-12 col-lg-9">
                    <div className="tab-content">
                        <div id="profile" className="container tab-pane active">
                            <Profile customer={customer} />
                        </div>
                        <div id="transaction" className="container tab-pane">
                            {customer&&<Transactions customer_id={customer.id} />}
                        </div>
                    </div>
                        
                    </div>
                </div>
                
            </div>
        </div>
    );
}

export default CustomerDetail;