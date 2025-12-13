import React from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import FloatingSmartCompanion from "./FloatingSmartCompanion";

const MainLayout: React.FC = () => {
    return (
        <>
            <Header />
            <main>
                <Outlet />
            </main>
            <FloatingSmartCompanion />
        </>
    );
};

export default MainLayout;
