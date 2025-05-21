'use client'

import { usePathname } from 'next/navigation'
import AuthGuard from '@/components/AuthGuard'
import './globals.css'

export default function RootLayout({ children }: { children: React.ReactNode }) {
    const path = usePathname()
    const publicPages = ['/login']

    return (
        <html lang="en">
        <body>
        {publicPages.includes(path)
            ? children
            : <AuthGuard>{children}</AuthGuard>
        }
        </body>
        </html>
    )
}
