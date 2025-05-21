"use client"

import { useRouter } from "next/navigation";
import { useState } from "react";
import axios, { AxiosError } from "axios";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formData = new URLSearchParams();
        formData.append("username", username);
        formData.append("password", password);

        try {
            const response = await axios.post("http://localhost:8080/login", formData, {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                withCredentials: true,
            });

            if (response.status === 200) {
                console.log("Login successful!");
                router.push("/");
            }
        } catch (error) {
            const err = error as AxiosError;
            if (error.response) {
                console.error("Login failed", err.response.status);
            } else {
                console.error("Network error", err.message);
            }
        }
    };

    return(
        <main className="min-h-screen flex items-center justify-center bg-[#F9FAFB]">
            <section className="bg-white rounded-xl shadow-[0_8px_20px_rgba(0,0,0,0.05)] max-w-md w-full p-8">
                <form onSubmit={handleSubmit} className="space-y-6">
                    <h1 className="text-center text-4xl font-bold">Medialabo</h1>
                    <input
                        type="text"
                        placeholder="Nom d'utilisateur"
                        className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg focus:outline-none"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="Mot de passe"
                        className="w-full px-4 py-2 border border-[#d1d5db] rounded-lg focus:outline-none"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button
                        type="submit"
                        className="w-full px-4 py-2 bg-blue-500 text-white rounded-lg cursor-pointer hover:bg-blue-600 focus:outline-none">
                        Login
                    </button>
                </form>
            </section>
        </main>
    )
}
