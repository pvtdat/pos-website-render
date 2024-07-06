import React,  { useEffect, useState, Fragment } from "react";
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { publicRouters, privateRouters, adminRouters, salerRouters, customerRouters} from './routes';
import axios from 'axios';
import { DefaultLayout } from "./components/Layout";
import LoadingScreen from "./components/LoadingScreen";

function App() {
	// Initial Authentication State
	const [user, setUser] = useState(null);
	const [loading, setLoading] = useState(true);
	const server_url = process.env.REACT_APP_API_ENDPOINT;
	const url = `${server_url}/api/account/`;

	useEffect(() => {
		// Check if a token exists in local storage
		const token = localStorage.getItem('token');
		if (token) {
			// Fetch user data
			axios.get(url, { headers: { 'Authorization': token } })
			.then(response => {
				const res = response.data;
				if (res.code !== 0) {
					// Invalid token
					localStorage.removeItem('token');
					setUser(null);
					setLoading(false);
					return;
				}else{
					setUser(res.data);

				}
				setLoading(false);
			})
			.catch(error => {
				console.error ('Error fetching user data', error);
				setLoading(false);

			});
		} else {
			setLoading(false);
		}
  	}, []);

	const handleRenderRouters = (routers) => {
		const paths = routers.map((route, index) => {
			let Layout = DefaultLayout;

			if (route.layout) {
				Layout = route.layout;
			} else if (route.layout === null) {
				Layout = Fragment;
			}

			const Page = route.element;

			const routeElement = Layout === Fragment
				? (
					<Layout key={index}>
						<Page />
					</Layout>
				)
				: (
					<Layout key={index} user={user}>
						<Page />
					</Layout>
				);

			const tempRole = !user && Layout !== Fragment
				? (
					<Route
						key={index}
						path={route.path}
						element={
							<Layout>
								<Page />
							</Layout>
						}
					/>
				)
				: (
					<Route
						key={index}
						path={route.path}
						element={routeElement}
					/>
				);

			return tempRole;
		});
		return paths;
	};

	if (loading) {
    	return <LoadingScreen />;
  	}
	return (
		<Router>
			<div className="App">
				<Routes>
					
					{user ? handleRenderRouters(privateRouters) : (
						<Route path="*" element={<Navigate to="/login" />} />
					)}
					{user && user.role === 'Administrator' && handleRenderRouters(adminRouters)}
					{user && user.role === 'Sale person' && handleRenderRouters(salerRouters)}
					{user && user.role === 'Customer' && handleRenderRouters(customerRouters)}
					{handleRenderRouters(publicRouters)}
				</Routes>
				{user && user.status === 'InActive' && <Navigate to='/renew-password' />}
			</div>
		</Router>
	);
}

export default App;