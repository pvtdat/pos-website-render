import React, { useState } from "react";
import './LoginRegister.css';
import axios from 'axios';
import {Link} from 'react-router-dom';
import LoadingImg from "../../../components/Layout/components/LoadingImg";

export const Login = (props) => {
    const [login, setLogin] = useState(false);
    const [error, setError] = useState(null);
    const [showPassword, setShowPassword] = useState(false);

    const togglePasswordVisibility = () => {
      setShowPassword(!showPassword);
    };
    function handleLogin(event) {
        event.preventDefault();
        setLogin(true);
        setError(null);
        
        const server_url = process.env.REACT_APP_API_ENDPOINT;
        const url = `${server_url}/api/account/login`;
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        axios.post(url, {username, password}, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
            .then(response => {
                const res = response.data;
                if (res.code === 0) {
                    const token = res.data.token;
                    localStorage.setItem('token', token);
                    window.location.href = '/';
                } else {
                    setError(res.message);
                }
                setLogin(false);
            })
            .catch(error => {
                setError(error);
                setLogin(false);
            });
    }

    return (
        <div className="wrapper fadeInDown d-flex align-items-center">
            <div id="formContent">
                {/* Tabs Titles */}
                <h2 className="active login_title">Sign In</h2>
                {/* Icon */}
                <div className="fadeIn first">
                <img 
                    src="https://firebasestorage.googleapis.com/v0/b/nodejs-final-8bdf4.appspot.com/o/javalogo.png?alt=media&token=799cf286-2d50-4384-9145-a2738bbabc28"
                    id="icon"
                    alt="User Icon"
                    className="my-3"
                    />
                </div>

                {/* Login Form */}
                <form>
                    <div className="input-container">
                    <input type="text" id="username" className="fadeIn second custom-input" name="username" placeholder="username" />
                    </div>
                    <div className="input-container">
                        <input
                            type={showPassword ? "text" : "password"}
                            id="password"
                            className="fadeIn third"
                            name="password"
                            placeholder="password"
                        />
                    </div>
                    <div className="fadeIn fourth form-input my-3" onClick={togglePasswordVisibility}>
                        <input checked={showPassword} type="checkbox" id="showPassword" className="form-check-input custom-input" />
                        <label className="form-check-label">Show Password</label>
                    </div>

                    
                    <input type="submit" id="login-btn" onClick={handleLogin} className="fadeIn fourth" value="Log In" disabled={login}/>
                    {error && <div className="alert alert-success mx-3 mt-2">
                        <strong>Error!</strong> {error}
                    </div>}
                </form>
                {login && <LoadingImg />}                

                {/* Remind Password */}
                <div id="formFooter" className="mt-3">
                    <a className="underlineHover" href="https://www.facebook.com/pvtd.2003/" target="_blank"
                       rel="noopener noreferrer">Operated by FinnIT.Com</a>
                </div>
            </div>
        </div>
    )
}

export default Login;