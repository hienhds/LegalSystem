import React from "react";
import Header from "./Header";
import Footer from "./Footer";

export default function Layout({ children, showFooter = false }) {
  return (
    <div className="bg-background-light dark:bg-background-dark font-display min-h-screen flex flex-col">
      <Header />
      <main className="flex-1">
        {children}
      </main>
      {showFooter && <Footer />}
    </div>
  );
}
