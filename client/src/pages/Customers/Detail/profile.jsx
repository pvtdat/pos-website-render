function Profile({customer}) {
    return (
        <div className="card py-5">
            <div className="card-body">
                <div className="row">
                    <div className="col-sm-12 col-md-5 d-flex align-items-center justify-content-center">
                        <img
                            className="card-img-top rounded-circle"
                            style={{width: '250px'}}
                            src={customer?.image}
                            alt="Profile"
                        />
                    </div>
                    <div className="col-sm-12 col-md-7">
                        <div className="form-group">
                            <label className="text-secondary"><strong>Full Name</strong></label>
                            <h5>{customer?.name}</h5>
                        </div>
                        <div className="form-group">
                            <label className="text-secondary"><strong>Phone</strong></label>
                            <h5>{customer?.phone}</h5>
                        </div>
                        <div className="form-group">
                            <label className="text-secondary"><strong>Address</strong></label>
                            <h5>{customer?.address}</h5>
                        </div>
                        <div className="form-group">
                            <label className="text-secondary"><strong>Creation Date</strong></label>
                            <h5>{customer ? new Date(customer.creation_date).toDateString() : ''}</h5>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Profile;