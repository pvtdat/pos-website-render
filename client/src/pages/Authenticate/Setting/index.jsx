import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

function Settings() {
    return (
        <div className="card mb-4">
            <div className="card-header bg-main text-main text-center">
                <h4 className="font-heading">SETTINGS</h4>
            </div>
            <div className="row mt-4 mx-3">
                {/* Sidebar for different settings categories */}
                <div className="col-md-3">
                    <div className="list-group">
                        <a href="#profile-settings" className="list-group-item list-group-item-action active">
                            Profile Settings
                        </a>
                        <a href="#payment-settings" className="list-group-item list-group-item-action">
                            Payment Settings
                        </a>
                        <a href="#store-settings" className="list-group-item list-group-item-action">
                            Store Settings
                        </a>
                        <a href="#notification-settings" className="list-group-item list-group-item-action">
                            Notification Settings
                        </a>
                    </div>
                </div>

                {/* Main content for each settings section */}
                <div className="col-md-9">
                    <div id="profile-settings" className="mb-5">
                        <h5>Profile Settings</h5>
                        <div className="card p-4">
                            <div className="form-group mb-3">
                                <label>Full Name</label>
                                <input type="text" className="form-control" placeholder="Enter full name"/>
                            </div>
                            <div className="form-group mb-3">
                                <label>Email</label>
                                <input type="email" className="form-control" placeholder="Enter email"/>
                            </div>
                            <div className="form-group mb-3">
                                <label>Password</label>
                                <input type="password" className="form-control" placeholder="Enter new password"/>
                            </div>
                            <button className="btn btn-primary">Save Changes</button>
                        </div>
                    </div>

                    <div id="payment-settings" className="mb-5">
                        <h5>Payment Settings</h5>
                        <div className="card p-4">
                            <div className="form-group mb-3">
                                <label>Payment Method</label>
                                <select className="form-control">
                                    <option>Credit Card</option>
                                    <option>PayPal</option>
                                    <option>Bank Transfer</option>
                                </select>
                            </div>
                            <div className="form-group mb-3">
                                <label>Card Number</label>
                                <input type="text" className="form-control" placeholder="Enter card number"/>
                            </div>
                            <button className="btn btn-primary">Save Changes</button>
                        </div>
                    </div>

                    <div id="store-settings" className="mb-5">
                        <h5>Store Settings</h5>
                        <div className="card p-4">
                            <div className="form-group mb-3">
                                <label>Store Name</label>
                                <input type="text" className="form-control" placeholder="Enter store name"/>
                            </div>
                            <div className="form-group mb-3">
                                <label>Store Address</label>
                                <input type="text" className="form-control" placeholder="Enter store address"/>
                            </div>
                            <div className="form-group mb-3">
                                <label>Store Phone Number</label>
                                <input type="text" className="form-control" placeholder="Enter store phone number"/>
                            </div>
                            <button className="btn btn-primary">Save Changes</button>
                        </div>
                    </div>

                    <div id="notification-settings" className="mb-5">
                        <h5>Notification Settings</h5>
                        <div className="card p-4">
                            <div className="form-group mb-3">
                                <label>Email Notifications</label>
                                <select className="form-control">
                                    <option>Enabled</option>
                                    <option>Disabled</option>
                                </select>
                            </div>
                            <div className="form-group mb-3">
                                <label>SMS Notifications</label>
                                <select className="form-control">
                                    <option>Enabled</option>
                                    <option>Disabled</option>
                                </select>
                            </div>
                            <button className="btn btn-primary">Save Changes</button>
                        </div>
                    </div>
                </div>
            </div>
            <h5 className="text-center text-info font-heading">Trial version, only view !</h5>

        </div>
    );
}

export default Settings;