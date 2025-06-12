'use client'

import React, { FC, ReactNode, useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import axios from 'axios'

interface AuthGuardProps {
    children: ReactNode
}

const AuthGuard: FC<AuthGuardProps> = ({ children }) => {
    const router = useRouter()
    const [isLoading, setIsLoading] = useState<boolean>(true)
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false)

    useEffect(() => {
        const checkAuth = async (): Promise<void> => {
            try {
                const response = await axios.get<void>(`${process.env.NEXT_PUBLIC_API_URL}/api/patient`, {
                    withCredentials: true,
                })

                if (response.status === 200 || response.status === 204) {
                    setIsAuthenticated(true)
                } else {
                    throw new Error(`Unexpected status code: ${response.status}`)
                }
            } catch (error) {
                if (axios.isAxiosError(error)) {
                    const status = error.response?.status
                    if (status === 401) {
                        console.warn('[AuthGuard] 401 Unauthorized – redirect to /login')
                    } else {
                        console.error('[AuthGuard] Axios error:', error)
                    }
                } else {
                    console.error('[AuthGuard] Unknown error:', error)
                }
                router.replace('/login')
            } finally {
                setIsLoading(false)
            }
        }

        checkAuth()
    }, [router])

    if (isLoading) {
        return <p>🔒 Chargement…</p>
    }

    if (!isAuthenticated) {
        return null
    }

    return <>{children}</>
}

export default AuthGuard
