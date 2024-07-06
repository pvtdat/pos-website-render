import axios from "axios";
import { useEffect, useState, useRef, Fragment } from "react";
import $ from 'jquery';
import LoadingImg from '../../../components/Layout/components/LoadingImg';
function Profile() {
  	const token = window.localStorage.getItem("token");
	const [user, setUser] = useState({});
	const [name, setName] = useState(null);
	const [image, setImage] = useState(null);
	const [url, setUrl] = useState(null);
	const [update, setUpdated] = useState(false);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState(null);
	const server_url = process.env.REACT_APP_API_ENDPOINT;
	const url_ = `${server_url}/api/account/`;

  // Fetch user for first time
  useEffect(() => {
    handleFetchUser();
  }, [image]);

  const handleFetchUser = async () => {
	axios
      .get(url_, { headers: { Authorization: token } })
      .then((response) => {
        const res = response.data;
        setUser(res.data);
		setName(res.data.name);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  const handleChangeImg = async (file) => {
	setImage(file);
	setUrl(URL.createObjectURL(file));
	setUpdated(true);
  }

  const handleChangeName = async (name) =>{
	setName(name);
	setUpdated(true);
  }

  const handleCancelUpdate = () => {
	setName(user.name);
	setUpdated(false);
	setUrl(null);
  }

	const handleUpdate = () => {
		setLoading(true);
		setError(null);

		const formData = new FormData();
		formData.append("name", name);
		formData.append("file", image);
		formData.append("username", user.username);

		axios.patch("/api/account/", formData, {
			headers: {
				Authorization: token,
			}
		})
			.then((response) => {
				const res = response.data;
				if(res.code === 0){
					$('#profileModal').modal();
					localStorage.removeItem('token');
					setUpdated(false);
				}else{
					setError(res.message);
				}
				setLoading(false);
				setUpdated(false);
			})
			.catch((error) => {
				setLoading(false);
				setUpdated(false);
				setError(error.message);
			});
	}
  	return (
		<Fragment>
			<div className="card mb-4">
				<div className="card-header bg-main text-main">
					<h4 className="font-heading">PROFILE INFORMATION</h4>
				</div>
				<div className="card-body">
					<div className="mt-2">
						<div className="row p-3">
							<div
								className="col-md-4 d-flex flex-column align-items-center justify-content-center border border-primary p-4 rounded">
								<img
									src={url || user.image}
									alt="Avatar"
									className="img-fluid rounded-circle mb-3"
									width="150"
									height="150"
								/>
								<div className="file-input-container btn btn-sm btn-outline-secondary">
									<input
										name="file"
										type="file"
										accept="image/*"
										className="file-input"
										onChange={(e) => handleChangeImg(e.target.files[0])}
										style={{display: 'none'}}
										id="fileInput"
									/>
									<label htmlFor="fileInput" className="mb-0">
										Update avatar <i className="fa-solid fa-pen-to-square"></i>
									</label>
								</div>
							</div>
							<div className="col-md-8 px-5">
								<div className="mb-4">
									<label className="form-label text-secondary"><strong>Full Name</strong></label>
									<input
										value={name || user.name}
										onChange={(e) => handleChangeName(e.target.value)}
										className="form-control mb-2"
									/>
								</div>
								<h5>
									<span className="badge bg-secondary me-2">Username</span>
									<span className="badge bg-info">{user.username}</span>
								</h5>
								<h5>
									<span className="badge bg-secondary me-2">Email</span>
									<span className="badge bg-info">{user.email}</span>
								</h5>
								<h5>
									<span className="badge bg-secondary me-2">Role</span>
									<span className="badge bg-info">{user.role}</span>
								</h5>
								<h5>
									<span className="badge bg-secondary me-2">Status</span>
									<span className="badge bg-info">{user.status}</span>
								</h5>
							</div>
						</div>
					</div>
				</div>
				{update && (
					<div className="card-footer">
						<div className="d-flex justify-content-end">
							<button
								type="button"
								className="btn btn-warning me-2"
								onClick={handleUpdate}
								disabled={loading}
							>
								<span>Update</span>
							</button>
							<button
								type="button"
								className="btn btn-primary"
								onClick={handleCancelUpdate}
								disabled={loading}
							>
								<i className="fa-solid fa-ban me-1"></i>Cancel
							</button>
						</div>
					</div>
				)}
				{loading && (
					<div className="text-center my-3">
						<LoadingImg/>
					</div>
				)}
				{error && (
					<div className="alert alert-danger text-center my-3">
						{error}
					</div>
				)}

				{/* The Modal */}
				<div className="modal fade" id="profileModal" tabIndex="-1" aria-labelledby="profileModalLabel"
					 aria-hidden="true">
					<div className="modal-dialog">
						<div className="modal-content">
							<div className="modal-header bg-primary text-white">
								<h4 className="modal-title" id="profileModalLabel">Profile Notification</h4>
								<button type="button" className="btn-close" data-bs-dismiss="modal"
										aria-label="Close"></button>
							</div>
							<div className="modal-body">
								Your account has been updated successfully! <br/>
								<strong>Please login again to see the changes.</strong>
							</div>
							<div className="modal-footer">
								<a href="/logout" className="btn btn-danger">Logout</a>
							</div>
						</div>
					</div>
				</div>
			</div>

		</Fragment>
	);
}

export default Profile;
