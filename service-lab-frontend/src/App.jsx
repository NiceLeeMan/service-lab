import './App.css'
import LoginPage from "./page/LoginPage";
import SocialSignupPage from "./page/SocialSignupPage";
import SignupPage from "./page/SignupPage";

export default function App() {
  if (window.location.pathname === "/social-signup") {
    return <SocialSignupPage />;
  }

  if (window.location.pathname === "/signup") {
    return <SignupPage />;
  }

  return <LoginPage />;
}
