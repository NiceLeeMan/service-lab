import './App.css'
import LoginPage from "./page/LoginPage";
import SocialSignupPage from "./page/SocialSignupPage";

export default function App() {
  if (window.location.pathname === "/social-signup") {
    return <SocialSignupPage />;
  }

  return <LoginPage />;
}
